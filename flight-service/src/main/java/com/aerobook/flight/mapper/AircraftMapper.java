package com.aerobook.flight.mapper;

import org.springframework.stereotype.Component;

import com.aerobook.flight.dto.response.AircraftResponse;
import com.aerobook.flight.entity.Aircraft;

/**
 * Mapper component converting {@link Aircraft} entities into
 * {@link AircraftResponse} DTOs.
 */
@Component
public class AircraftMapper {

    /**
     * Converts an {@link Aircraft} JPA entity into an {@link AircraftResponse}
     * transfer object.
     *
     * @param aircraft the source aircraft entity
     * @return populated response DTO, or null if entity is null
     */
    public AircraftResponse toResponse(Aircraft aircraft) {

        if (aircraft == null) {
            return null;
        }

        AircraftResponse response = new AircraftResponse();

        response.setId(aircraft.getId());
        response.setAircraftCode(aircraft.getAircraftCode());
        response.setAircraftName(aircraft.getAircraftName());
        response.setManufacturer(aircraft.getManufacturer());
        response.setCapacity(aircraft.getCapacity());

        if (aircraft.getStatus() != null) {
            response.setStatus(aircraft.getStatus().name());
        }

        return response;
    }
}
