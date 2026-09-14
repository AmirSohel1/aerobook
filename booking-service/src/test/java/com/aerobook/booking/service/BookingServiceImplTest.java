package com.aerobook.booking.service;

import com.aerobook.booking.client.FlightServiceClient;
import com.aerobook.booking.dto.BookingRequest;
import com.aerobook.booking.dto.BookingResponse;
import com.aerobook.booking.dto.PassengerRequest;
import com.aerobook.booking.entity.Booking;
import com.aerobook.booking.entity.enums.BookingStatus;
import com.aerobook.booking.exception.ResourceNotFoundException;
import com.aerobook.booking.messaging.BookingEventPublisher;
import com.aerobook.booking.repository.BookingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private BookingEventPublisher bookingEventPublisher;

    @Mock
    private FlightServiceClient flightServiceClient;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private FlightServiceClient.FlightResponse mockFlight;
    private BookingRequest mockBookingRequest;

    @BeforeEach
    void setUp() {
        mockFlight = new FlightServiceClient.FlightResponse();
        mockFlight.setFlightId(101L);
        mockFlight.setFlightNumber("AI101");
        mockFlight.setAirlineName("Air India");
        mockFlight.setSource("DEL");
        mockFlight.setDestination("BOM");
        mockFlight.setStatus("SCHEDULED");
        mockFlight.setAvailableSeats(50);
        mockFlight.setTotalSeats(180);
        mockFlight.setBaseFare(BigDecimal.valueOf(4500));
        mockFlight.setDepartureTime(LocalDateTime.now().plusDays(2));
        mockFlight.setArrivalTime(LocalDateTime.now().plusDays(2).plusHours(2));

        PassengerRequest passenger = new PassengerRequest();
        passenger.setFirstName("Asha");
        passenger.setLastName("Khan");
        passenger.setAge(29);
        passenger.setGender("F");

        mockBookingRequest = new BookingRequest();
        mockBookingRequest.setUserId(1L);
        mockBookingRequest.setFlightId(101L);
        mockBookingRequest.setPassengers(List.of(passenger));
    }

    @Test
    @DisplayName("Should successfully create a booking when flight has seats and is departing in the future")
    void createBooking_Success() {
        when(flightServiceClient.getFlightById(101L)).thenReturn(mockFlight);
        when(bookingRepository.save(any(Booking.class))).thenAnswer(inv -> {
            Booking b = inv.getArgument(0);
            b.setBookingId(1L);
            return b;
        });

        BookingResponse response = bookingService.createBooking(mockBookingRequest);

        assertThat(response).isNotNull();
        assertThat(response.getBookingId()).isEqualTo(1L);
        assertThat(response.getPnr()).isNotBlank();
        assertThat(response.getUserId()).isEqualTo(1L);
        assertThat(response.getFlightId()).isEqualTo(101L);
        assertThat(response.getStatus()).isEqualTo(BookingStatus.BOOKED.name());
        assertThat(response.getTotalFare()).isEqualByComparingTo(BigDecimal.valueOf(4500));
        assertThat(response.getPassengers()).hasSize(1);
        assertThat(response.getFlightNumber()).isEqualTo("AI101");
        assertThat(response.getAirlineName()).isEqualTo("Air India");

        verify(bookingEventPublisher, times(1)).publishCreated(response.getPnr());
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    @DisplayName("Should reject booking creation when passenger list is null or empty")
    void createBooking_ThrowsException_WhenPassengersEmpty() {
        mockBookingRequest.setPassengers(Collections.emptyList());

        assertThatThrownBy(() -> bookingService.createBooking(mockBookingRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("At least one passenger is required");

        verifyNoInteractions(flightServiceClient);
        verifyNoInteractions(bookingRepository);
    }

    @Test
    @DisplayName("Should reject booking when flight does not exist in flight-service")
    void createBooking_ThrowsException_WhenFlightNotFound() {
        when(flightServiceClient.getFlightById(101L)).thenReturn(null);

        assertThatThrownBy(() -> bookingService.createBooking(mockBookingRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Flight not found with ID: 101");

        verifyNoInteractions(bookingRepository);
        verifyNoInteractions(bookingEventPublisher);
    }

    @Test
    @DisplayName("Business Rule: Reject booking when flight departure time has already passed")
    void createBooking_ThrowsException_WhenFlightDepartureInPast() {
        mockFlight.setDepartureTime(LocalDateTime.now().minusHours(3));
        when(flightServiceClient.getFlightById(101L)).thenReturn(mockFlight);

        assertThatThrownBy(() -> bookingService.createBooking(mockBookingRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Departure time")
                .hasMessageContaining("has already passed");

        verifyNoInteractions(bookingRepository);
    }

    @Test
    @DisplayName("Business Rule: Reject booking when flight is CANCELLED")
    void createBooking_ThrowsException_WhenFlightCancelled() {
        mockFlight.setStatus("CANCELLED");
        when(flightServiceClient.getFlightById(101L)).thenReturn(mockFlight);

        assertThatThrownBy(() -> bookingService.createBooking(mockBookingRequest))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Flight is currently CANCELLED");

        verifyNoInteractions(bookingRepository);
    }

    @Test
    @DisplayName("Business Rule: Reject booking when flight is COMPLETED")
    void createBooking_ThrowsException_WhenFlightCompleted() {
        mockFlight.setStatus("COMPLETED");
        when(flightServiceClient.getFlightById(101L)).thenReturn(mockFlight);

        assertThatThrownBy(() -> bookingService.createBooking(mockBookingRequest))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Flight is currently COMPLETED");

        verifyNoInteractions(bookingRepository);
    }

    @Test
    @DisplayName("Business Rule: Reject booking when party size exceeds available seats")
    void createBooking_ThrowsException_WhenInsufficientSeats() {
        mockFlight.setAvailableSeats(1);

        PassengerRequest p1 = new PassengerRequest();
        p1.setFirstName("Asha");
        p1.setLastName("Khan");
        p1.setAge(29);
        p1.setGender("F");

        PassengerRequest p2 = new PassengerRequest();
        p2.setFirstName("Rahul");
        p2.setLastName("Sharma");
        p2.setAge(31);
        p2.setGender("M");

        mockBookingRequest.setPassengers(List.of(p1, p2));
        when(flightServiceClient.getFlightById(101L)).thenReturn(mockFlight);

        assertThatThrownBy(() -> bookingService.createBooking(mockBookingRequest))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Insufficient seats available")
                .hasMessageContaining("Requested: 2, Available: 1");

        verifyNoInteractions(bookingRepository);
    }

    @Test
    @DisplayName("Should successfully retrieve booking by ID")
    void getBookingById_Success() {
        Booking booking = new Booking();
        booking.setBookingId(1L);
        booking.setPnr("AB1001");
        booking.setUserId(1L);
        booking.setFlightId(101L);
        booking.setStatus(BookingStatus.BOOKED);
        booking.setTotalFare(BigDecimal.valueOf(4500));

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(flightServiceClient.getFlightById(101L)).thenReturn(mockFlight);

        BookingResponse response = bookingService.getBookingById(1L);

        assertThat(response).isNotNull();
        assertThat(response.getBookingId()).isEqualTo(1L);
        assertThat(response.getPnr()).isEqualTo("AB1001");
        assertThat(response.getFlightNumber()).isEqualTo("AI101");
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when booking ID is not found")
    void getBookingById_NotFound() {
        when(bookingRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.getBookingById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Booking not found with ID: 999");
    }

    @Test
    @DisplayName("Should successfully retrieve booking by PNR")
    void getBookingByPnr_Success() {
        Booking booking = new Booking();
        booking.setBookingId(1L);
        booking.setPnr("AB1001");
        booking.setUserId(1L);
        booking.setFlightId(101L);
        booking.setStatus(BookingStatus.BOOKED);

        when(bookingRepository.findByPnr("AB1001")).thenReturn(Optional.of(booking));
        when(flightServiceClient.getFlightById(101L)).thenReturn(mockFlight);

        BookingResponse response = bookingService.getBookingByPnr("AB1001");

        assertThat(response).isNotNull();
        assertThat(response.getPnr()).isEqualTo("AB1001");
    }

    @Test
    @DisplayName("Should retrieve bookings by User ID")
    void getBookingsByUserId_Success() {
        Booking b1 = new Booking();
        b1.setBookingId(1L);
        b1.setPnr("AB1001");
        b1.setUserId(1L);
        b1.setStatus(BookingStatus.BOOKED);

        when(bookingRepository.findByUserId(1L)).thenReturn(List.of(b1));

        List<BookingResponse> responses = bookingService.getBookingsByUserId(1L);

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getPnr()).isEqualTo("AB1001");
    }

    @Test
    @DisplayName("Should retrieve all bookings across the airline (Admin operation)")
    void getAllBookings_Success() {
        Booking b1 = new Booking();
        b1.setBookingId(1L);
        b1.setPnr("AB1001");
        b1.setStatus(BookingStatus.BOOKED);

        Booking b2 = new Booking();
        b2.setBookingId(2L);
        b2.setPnr("AB1002");
        b2.setStatus(BookingStatus.BOOKED);

        when(bookingRepository.findAll()).thenReturn(List.of(b1, b2));

        List<BookingResponse> responses = bookingService.getAllBookings();

        assertThat(responses).hasSize(2);
    }

    @Test
    @DisplayName("Should successfully cancel an active booking")
    void cancelBooking_Success() {
        Booking booking = new Booking();
        booking.setBookingId(1L);
        booking.setPnr("AB1001");
        booking.setStatus(BookingStatus.BOOKED);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        String result = bookingService.cancelBooking(1L);

        assertThat(result).isEqualTo("Booking Cancelled Successfully");
        assertThat(booking.getStatus()).isEqualTo(BookingStatus.CANCELLED);
        verify(bookingRepository, times(1)).save(booking);
    }

    @Test
    @DisplayName("Business Rule: Reject cancellation if booking is already CANCELLED")
    void cancelBooking_ThrowsException_WhenAlreadyCancelled() {
        Booking booking = new Booking();
        booking.setBookingId(1L);
        booking.setStatus(BookingStatus.CANCELLED);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> bookingService.cancelBooking(1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("is already cancelled");

        verify(bookingRepository, never()).save(any());
    }
}
