package com.aerobook.flight.service;

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
import com.aerobook.flight.service.impl.FlightServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit test suite validating flight lifecycle, updates, deletions, and error
 * conditions in {@link FlightServiceImpl}.
 */
@ExtendWith(MockitoExtension.class)
class FlightServiceImplTest {

    @Mock
    private FlightRepository flightRepository;

    @Mock
    private AircraftRepository aircraftRepository;

    @InjectMocks
    private FlightServiceImpl flightService;

    private Aircraft testAircraft;
    private Flight testFlight;
    private CreateFlightRequest createRequest;

    @BeforeEach
    void setUp() {
        testAircraft = new Aircraft();
        testAircraft.setId(1L);
        testAircraft.setAircraftCode("A320-001");
        testAircraft.setAircraftName("Airbus A320");
        testAircraft.setManufacturer("Airbus");
        testAircraft.setCapacity(180);
        testAircraft.setStatus(com.aerobook.flight.enums.AircraftStatus.ACTIVE);

        testFlight = new Flight();
        testFlight.setId(10L);
        testFlight.setFlightNumber("AI101");
        testFlight.setAirlineName("Air India");
        testFlight.setSource("Mumbai");
        testFlight.setDestination("Delhi");
        testFlight.setDepartureTime(LocalDateTime.now().plusDays(2));
        testFlight.setArrivalTime(LocalDateTime.now().plusDays(2).plusHours(2));
        testFlight.setTotalSeats(180);
        testFlight.setAvailableSeats(180);
        testFlight.setBaseFare(BigDecimal.valueOf(4500));
        testFlight.setStatus(FlightStatus.SCHEDULED);
        testFlight.setAircraft(testAircraft);

        createRequest = new CreateFlightRequest();
        createRequest.setFlightNumber("AI101");
        createRequest.setAirlineName("Air India");
        createRequest.setSource("Mumbai");
        createRequest.setDestination("Delhi");
        createRequest.setDepartureTime(LocalDateTime.now().plusDays(2));
        createRequest.setArrivalTime(LocalDateTime.now().plusDays(2).plusHours(2));
        createRequest.setTotalSeats(180);
        createRequest.setBaseFare(BigDecimal.valueOf(4500));
        createRequest.setAircraftId(1L);
    }

    @Test
    void createFlight_Success_SavesAndReturnsFlightResponse() {
        when(aircraftRepository.findById(1L)).thenReturn(Optional.of(testAircraft));
        when(flightRepository.save(any(Flight.class))).thenReturn(testFlight);

        FlightResponse response = flightService.createFlight(createRequest);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(10L);
        assertThat(response.getFlightNumber()).isEqualTo("AI101");
        assertThat(response.getSource()).isEqualTo("Mumbai");
        assertThat(response.getDestination()).isEqualTo("Delhi");
        assertThat(response.getStatus()).isEqualTo(FlightStatus.SCHEDULED.name());
        verify(flightRepository).save(any(Flight.class));
    }

    @Test
    void createFlight_AircraftNotFound_ThrowsException() {
        when(aircraftRepository.findById(99L)).thenReturn(Optional.empty());
        createRequest.setAircraftId(99L);

        assertThatThrownBy(() -> flightService.createFlight(createRequest))
                .isInstanceOf(AircraftNotFoundException.class)
                .hasMessageContaining("Aircraft not found with id : 99");

        verify(flightRepository, never()).save(any(Flight.class));
    }

    @Test
    void getFlightById_Success_ReturnsFlightDetails() {
        when(flightRepository.findById(10L)).thenReturn(Optional.of(testFlight));

        FlightResponse response = flightService.getFlightById(10L);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(10L);
        assertThat(response.getFlightNumber()).isEqualTo("AI101");
        assertThat(response.getBaseFare()).isEqualByComparingTo("4500");
    }

    @Test
    void getFlightById_NotFound_ThrowsFlightNotFoundException() {
        when(flightRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> flightService.getFlightById(999L))
                .isInstanceOf(FlightNotFoundException.class)
                .hasMessageContaining("Flight not found with id : 999");
    }

    @Test
    void getAllFlights_ReturnsListOfFlights() {
        when(flightRepository.findAll()).thenReturn(List.of(testFlight));

        List<FlightResponse> flights = flightService.getAllFlights();

        assertThat(flights).hasSize(1);
        assertThat(flights.get(0).getFlightNumber()).isEqualTo("AI101");
    }

    @Test
    void deleteFlight_Success_DeletesFlight() {
        when(flightRepository.existsById(10L)).thenReturn(true);
        doNothing().when(flightRepository).deleteById(10L);

        flightService.deleteFlight(10L);

        verify(flightRepository).deleteById(10L);
    }
}
