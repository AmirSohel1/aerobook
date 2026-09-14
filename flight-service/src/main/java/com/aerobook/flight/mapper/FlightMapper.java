package com.aerobook.flight.mapper;

import org.springframework.stereotype.Component;

import com.aerobook.flight.dto.response.FlightResponse;
import com.aerobook.flight.entity.Flight;

/**
 * Mapper class for converting Flight entity into FlightResponse DTO.
 */
@Component
public class FlightMapper {

    /**
     * Converts Flight entity to FlightResponse DTO.
     *
     * @param flight Flight entity
     * @return FlightResponse DTO
     */
    public FlightResponse toResponse(Flight flight) {

        if (flight == null) {
            return null;
        }

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
