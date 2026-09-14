package com.aerobook.flight.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.aerobook.flight.dto.request.CreateFlightRequest;
import com.aerobook.flight.dto.request.UpdateFlightRequest;
import com.aerobook.flight.dto.response.FlightResponse;
import com.aerobook.flight.entity.Aircraft;
import com.aerobook.flight.entity.Flight;
import com.aerobook.flight.enums.FlightStatus;
import com.aerobook.flight.exception.AircraftNotFoundException;
import com.aerobook.flight.exception.FlightNotFoundException;
import com.aerobook.flight.repository.AircraftRepository;
import com.aerobook.flight.repository.FlightRepository;
import com.aerobook.flight.service.FlightService;

/**
 * Service implementation for flight management operations.
 *
 * This class contains business logic related to: - Flight creation - Flight
 * update - Flight deletion - Flight retrieval - Conversion of Entity to DTO
 */
@Service
public class FlightServiceImpl implements FlightService {

    private static final Logger log = LoggerFactory.getLogger(FlightServiceImpl.class);

    private final FlightRepository flightRepository;
    private final AircraftRepository aircraftRepository;

    /**
     * Constructor-based dependency injection.
     *
     * @param flightRepository Flight repository
     * @param aircraftRepository Aircraft repository
     */
    public FlightServiceImpl(
            FlightRepository flightRepository,
            AircraftRepository aircraftRepository) {

        this.flightRepository = flightRepository;
        this.aircraftRepository = aircraftRepository;
    }

    /**
     * Creates a new flight.
     *
     * @param request Flight creation request
     * @return Created flight details
     */
    @Override
    public FlightResponse createFlight(CreateFlightRequest request) {

        Aircraft aircraft = aircraftRepository.findById(request.getAircraftId())
                .orElseThrow(()
                        -> new AircraftNotFoundException(
                        "Aircraft not found with id : "
                        + request.getAircraftId()));

        Flight flight = new Flight();

        flight.setFlightNumber(request.getFlightNumber());
        flight.setAirlineName(request.getAirlineName());
        flight.setSource(request.getSource());
        flight.setDestination(request.getDestination());
        flight.setDepartureTime(request.getDepartureTime());
        flight.setArrivalTime(request.getArrivalTime());

        /*
         * Total seats are initialized from request.
         * Available seats are initially equal to total seats.
         */
        flight.setTotalSeats(request.getTotalSeats());
        flight.setAvailableSeats(request.getTotalSeats());

        flight.setBaseFare(request.getBaseFare());
        flight.setStatus(FlightStatus.SCHEDULED);
        flight.setAircraft(aircraft);

        Flight savedFlight = flightRepository.save(flight);
        log.info("Created scheduled flight: {} ({}) from {} to {} with {} total seats [Flight ID: {}]",
                savedFlight.getFlightNumber(), savedFlight.getAirlineName(),
                savedFlight.getSource(), savedFlight.getDestination(),
                savedFlight.getTotalSeats(), savedFlight.getId());

        return mapToResponse(savedFlight);
    }

    /**
     * Fetch flight details by id.
     *
     * @param id Flight id
     * @return Flight details
     */
    @Override
    public FlightResponse getFlightById(Long id) {

        log.debug("Fetching flight details for ID: {}", id);
        Flight flight = flightRepository
                .findById(id)
                .orElseThrow(()
                        -> new FlightNotFoundException(
                        "Flight not found with id : " + id));

        return mapToResponse(flight);
    }

    /**
     * Fetch all flights available in the system.
     *
     * @return List of flights
     */
    @Override
    public List<FlightResponse> getAllFlights() {

        return flightRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Updates an existing flight.
     *
     * @param id Flight id
     * @param request Updated flight information
     * @return Updated flight details
     */
    @Override
    public FlightResponse updateFlight(
            Long id,
            UpdateFlightRequest request) {

        Flight flight = flightRepository
                .findById(id)
                .orElseThrow(()
                        -> new FlightNotFoundException(
                        "Flight not found with id : " + id));

        flight.setAirlineName(request.getAirlineName());
        flight.setSource(request.getSource());
        flight.setDestination(request.getDestination());
        flight.setDepartureTime(request.getDepartureTime());
        flight.setArrivalTime(request.getArrivalTime());

        if (request.getTotalSeats() != null) {
            flight.setTotalSeats(request.getTotalSeats());
        }

        if (request.getBaseFare() != null) {
            flight.setBaseFare(request.getBaseFare());
        }

        Flight updatedFlight = flightRepository.save(flight);
        log.info("Updated flight schedule for Flight ID: {} (Flight Number: {})", id, updatedFlight.getFlightNumber());

        return mapToResponse(updatedFlight);
    }

    /**
     * Deletes a flight.
     *
     * @param id Flight id
     */
    @Override
    public void deleteFlight(Long id) {

        if (!flightRepository.existsById(id)) {
            throw new FlightNotFoundException(
                    "Flight not found with id : " + id);
        }

        flightRepository.deleteById(id);
        log.info("Deleted flight schedule with Flight ID: {}", id);
    }

    /**
     * Converts Flight Entity to FlightResponse DTO.
     *
     * @param flight Flight entity
     * @return FlightResponse DTO
     */
    private FlightResponse mapToResponse(Flight flight) {

        FlightResponse response = new FlightResponse();

        response.setId(flight.getId());
        response.setFlightNumber(flight.getFlightNumber());
        response.setAirlineName(flight.getAirlineName());
        response.setSource(flight.getSource());
        response.setDestination(flight.getDestination());
        response.setDepartureTime(flight.getDepartureTime());
        response.setArrivalTime(flight.getArrivalTime());
        response.setTotalSeats(flight.getTotalSeats());
        response.setAvailableSeats(flight.getAvailableSeats());
        response.setBaseFare(flight.getBaseFare());

        if (flight.getStatus() != null) {
            response.setStatus(flight.getStatus().name());
        }

        return response;
    }
}
