package com.aerobook.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.aerobook.booking.dto.BookingRequest;
import com.aerobook.booking.dto.PassengerRequest;
import com.aerobook.booking.client.FlightServiceClient;
import com.aerobook.booking.entity.Booking;
import com.aerobook.booking.messaging.BookingEventPublisher;
import com.aerobook.booking.repository.BookingRepository;
import com.aerobook.booking.service.BookingServiceImpl;

import java.util.List;
import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceApplicationTests {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private BookingEventPublisher bookingEventPublisher;

    @Mock
    private FlightServiceClient flightServiceClient;

    @Test
    void createBookingPersistsBookingAndPublishesEvent() {
        PassengerRequest passenger = new PassengerRequest();
        passenger.setFirstName("Asha");
        passenger.setLastName("Khan");
        passenger.setAge(29);
        passenger.setGender("F");

        BookingRequest request = new BookingRequest();
        request.setUserId(7L);
        request.setFlightId(11L);
        request.setPassengers(List.of(passenger));
        FlightServiceClient.FlightResponse flight
                = new FlightServiceClient.FlightResponse();
        flight.setBaseFare(BigDecimal.valueOf(5000));
        flight.setDepartureTime(java.time.LocalDateTime.now().plusDays(1));
        flight.setStatus("SCHEDULED");
        flight.setAvailableSeats(100);
        when(flightServiceClient.getFlightById(11L)).thenReturn(flight);
        when(bookingRepository.save(any(Booking.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var response = new BookingServiceImpl(
                bookingRepository, bookingEventPublisher,
                flightServiceClient).createBooking(request);

        assertThat(response.getUserId()).isEqualTo(7L);
        assertThat(response.getFlightId()).isEqualTo(11L);
        assertThat(response.getPnr()).isNotBlank();
        verify(bookingEventPublisher).publishCreated(response.getPnr());
    }

}
