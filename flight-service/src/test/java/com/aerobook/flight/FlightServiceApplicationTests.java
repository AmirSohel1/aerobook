package com.aerobook.flight;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.aerobook.flight.dto.request.CreateAircraftRequest;
import com.aerobook.flight.entity.Aircraft;
import com.aerobook.flight.repository.AircraftRepository;
import com.aerobook.flight.service.impl.AircraftServiceImpl;

/**
 * Unit tests verifying basic flight service initialization and aircraft
 * creation.
 */
@ExtendWith(MockitoExtension.class)
class FlightServiceApplicationTests {

    @Mock
    private AircraftRepository aircraftRepository;

    /**
     * Verifies that creating an aircraft sets its status to ACTIVE and saves it
     * in the repository.
     */
    @Test
    void createAircraftSavesActiveAircraft() {
        CreateAircraftRequest request = new CreateAircraftRequest(
                "A320", "Airbus A320", "Airbus", 180);
        Aircraft savedAircraft = new Aircraft();
        when(aircraftRepository.save(any(Aircraft.class))).thenReturn(savedAircraft);

        Aircraft result = new AircraftServiceImpl(aircraftRepository)
                .createAircraft(request);

        assertThat(result).isSameAs(savedAircraft);
        verify(aircraftRepository).save(any(Aircraft.class));
    }

}
