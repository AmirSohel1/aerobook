package com.aerobook.booking.service;

import com.aerobook.booking.client.FlightServiceClient;
import com.aerobook.booking.dto.BookingRequest;
import com.aerobook.booking.dto.BookingResponse;
import com.aerobook.booking.dto.PassengerRequest;
import com.aerobook.booking.dto.PassengerResponse;
import com.aerobook.booking.entity.Booking;
import com.aerobook.booking.entity.Passenger;
import com.aerobook.booking.entity.enums.BookingStatus;
import com.aerobook.booking.exception.ResourceNotFoundException;
import com.aerobook.booking.messaging.BookingEventPublisher;
import com.aerobook.booking.repository.BookingRepository;
import com.aerobook.booking.util.PnrGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Service implementation managing booking business logic, flight validation,
 * date validations, seat counts, PNR generation, and lifecycle events.
 */
@Service
public class BookingServiceImpl implements BookingService {

    private static final Logger log = LoggerFactory.getLogger(BookingServiceImpl.class);

    /**
     * JPA repository managing CRUD persistence operations for bookings.
     */
    private final BookingRepository bookingRepository;

    /**
     * Asynchronous publisher dispatching booking lifecycle events to RabbitMQ.
     */
    private final BookingEventPublisher bookingEventPublisher;

    /**
     * Feign client for flight validation and metadata retrieval.
     */
    private final FlightServiceClient flightServiceClient;

    /**
     * Constructs a new {@link BookingServiceImpl} with required dependencies.
     *
     * @param bookingRepository booking JPA repository
     * @param bookingEventPublisher message event publisher
     * @param flightServiceClient OpenFeign client for flight-service
     */
    public BookingServiceImpl(
            BookingRepository bookingRepository,
            BookingEventPublisher bookingEventPublisher,
            FlightServiceClient flightServiceClient) {
        this.bookingRepository = bookingRepository;
        this.bookingEventPublisher = bookingEventPublisher;
        this.flightServiceClient = flightServiceClient;
    }

    @Override
    public BookingResponse createBooking(BookingRequest request) {
        // 1. Validate passenger party size
        if (request.getPassengers() == null || request.getPassengers().isEmpty()) {
            throw new IllegalArgumentException("At least one passenger is required to create a booking.");
        }

        // 2. Fetch and validate flight existence via OpenFeign
        FlightServiceClient.FlightResponse flight = flightServiceClient.getFlightById(request.getFlightId());
        if (flight == null) {
            throw new ResourceNotFoundException("Flight not found with ID: " + request.getFlightId());
        }

        // 3. Date validation: Flight departure must not be in the past
        if (flight.getDepartureTime() != null && flight.getDepartureTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException(String.format(
                    "Cannot book flight: Departure time (%s) has already passed.",
                    flight.getDepartureTime()));
        }

        // 4. Flight status validation: Must not be CANCELLED or COMPLETED
        if (flight.getStatus() != null && ("CANCELLED".equalsIgnoreCase(flight.getStatus()) || "COMPLETED".equalsIgnoreCase(flight.getStatus()))) {
            throw new IllegalStateException(String.format(
                    "Cannot book flight: Flight is currently %s.",
                    flight.getStatus()));
        }

        // 5. Seat availability validation: Flight must have sufficient available seats
        int requestedSeats = request.getPassengers().size();
        if (flight.getAvailableSeats() != null && flight.getAvailableSeats() < requestedSeats) {
            throw new IllegalStateException(String.format(
                    "Insufficient seats available on flight %s. Requested: %d, Available: %d.",
                    flight.getFlightNumber() != null ? flight.getFlightNumber() : String.valueOf(request.getFlightId()),
                    requestedSeats,
                    flight.getAvailableSeats()));
        }

        // 6. Assemble Booking entity
        Booking booking = new Booking();
        booking.setPnr(PnrGenerator.generatePnr());
        booking.setUserId(request.getUserId());
        booking.setFlightId(request.getFlightId());
        booking.setBookingDate(LocalDateTime.now());
        booking.setStatus(BookingStatus.BOOKED);

        // Compute total fare based on flight baseFare and passenger count
        BigDecimal baseFare = flight.getBaseFare() != null
                ? flight.getBaseFare()
                : BigDecimal.valueOf(5000);
        booking.setTotalFare(baseFare.multiply(BigDecimal.valueOf(requestedSeats)));

        // Map passengers
        List<Passenger> passengers = new ArrayList<>();
        for (PassengerRequest pr : request.getPassengers()) {
            Passenger passenger = new Passenger();
            passenger.setFirstName(pr.getFirstName());
            passenger.setLastName(pr.getLastName());
            passenger.setAge(pr.getAge());
            passenger.setGender(pr.getGender());
            passenger.setBooking(booking);
            passengers.add(passenger);
        }
        booking.setPassengers(passengers);

        // 7. Persist booking and publish lifecycle event
        Booking savedBooking = bookingRepository.save(booking);
        bookingEventPublisher.publishCreated(savedBooking.getPnr());

        log.info("Confirmed flight booking with PNR: {} for User ID: {}", savedBooking.getPnr(), savedBooking.getUserId());
        return convertToResponse(savedBooking, flight);
    }

    @Override
    public BookingResponse getBookingById(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with ID: " + bookingId));
        return convertToResponse(booking, fetchFlightQuietly(booking.getFlightId()));
    }

    @Override
    public BookingResponse getBookingByPnr(String pnr) {
        Booking booking = bookingRepository.findByPnr(pnr)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with PNR: " + pnr));
        return convertToResponse(booking, fetchFlightQuietly(booking.getFlightId()));
    }

    @Override
    public List<BookingResponse> getBookingsByUserId(Long userId) {
        return bookingRepository.findByUserId(userId).stream()
                .map(booking -> convertToResponse(booking, null))
                .toList();
    }

    @Override
    public List<BookingResponse> getAllBookings() {
        return bookingRepository.findAll().stream()
                .map(booking -> convertToResponse(booking, null))
                .toList();
    }

    @Override
    public String cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new IllegalStateException("Booking with ID " + bookingId + " is already cancelled.");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
        log.info("Cancelled booking with ID: {} (PNR: {})", bookingId, booking.getPnr());
        return "Booking Cancelled Successfully";
    }

    /**
     * Safely attempts to fetch flight metadata via OpenFeign without
     * interrupting booking read flows if flight-service is temporarily
     * unavailable.
     *
     * @param flightId the flight ID to look up
     * @return flight details if found, or null on error
     */
    private FlightServiceClient.FlightResponse fetchFlightQuietly(Long flightId) {
        if (flightId == null) {
            return null;
        }
        try {
            return flightServiceClient.getFlightById(flightId);
        } catch (Exception ex) {
            log.warn("Unable to fetch flight details for flightId {}: {}", flightId, ex.getMessage());
            return null;
        }
    }

    /**
     * Transforms internal {@link Booking} and {@link Passenger} entities into a
     * public {@link BookingResponse}, enriching with flight schedule details
     * when available.
     *
     * @param booking the booking JPA entity
     * @param flight optional flight response metadata
     * @return populated {@link BookingResponse}
     */
    private BookingResponse convertToResponse(Booking booking, FlightServiceClient.FlightResponse flight) {
        BookingResponse response = new BookingResponse();
        response.setBookingId(booking.getBookingId());
        response.setPnr(booking.getPnr());
        response.setUserId(booking.getUserId());
        response.setFlightId(booking.getFlightId());
        response.setBookingDate(booking.getBookingDate());
        response.setTotalFare(booking.getTotalFare());
        response.setStatus(booking.getStatus() != null ? booking.getStatus().name() : null);

        // Map passengers
        if (booking.getPassengers() != null) {
            List<PassengerResponse> passengerResponses = booking.getPassengers().stream().map(p -> {
                PassengerResponse pr = new PassengerResponse();
                pr.setPassengerId(p.getPassengerId());
                pr.setFirstName(p.getFirstName());
                pr.setLastName(p.getLastName());
                pr.setAge(p.getAge());
                pr.setGender(p.getGender());
                return pr;
            }).toList();
            response.setPassengers(passengerResponses);
        }

        // Enrich with flight metadata if available
        if (flight != null) {
            response.setFlightNumber(flight.getFlightNumber());
            response.setAirlineName(flight.getAirlineName());
            response.setSource(flight.getSource());
            response.setDestination(flight.getDestination());
            response.setDepartureTime(flight.getDepartureTime());
            response.setArrivalTime(flight.getArrivalTime());
        }

        return response;
    }
}
