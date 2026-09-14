package com.aerobook.flight.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.aerobook.flight.dto.response.FlightResponse;
import com.aerobook.flight.entity.Flight;
import com.aerobook.flight.repository.FlightRepository;
import com.aerobook.flight.service.FlightSearchService;

/**
 * Service implementation for flight search operations.
 *
 * This service searches flights based on source and destination and converts
 * entity objects into response DTOs.
 */
@Service
public class FlightSearchServiceImpl implements FlightSearchService {

    private static final Logger log = LoggerFactory.getLogger(FlightSearchServiceImpl.class);

    private final FlightRepository flightRepository;

    /**
     * Constructor-based dependency injection.
     *
     * @param flightRepository Flight repository
     */
    public FlightSearchServiceImpl(FlightRepository flightRepository) {
        this.flightRepository = flightRepository;
    }

    /**
     * Search flights by source and destination.
     *
     * @param source departure location
     * @param destination arrival location
     * @return list of matching flights
     */
    @Override
    public List<FlightResponse> searchFlights(
            String source,
            String destination) {

        log.info("Searching flights route: '{}' -> '{}'", source, destination);
        List<FlightResponse> responses = flightRepository
                .findBySourceAndDestination(source, destination)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        log.debug("Found {} available flight(s) matching route: '{}' -> '{}'", responses.size(), source, destination);
        return responses;
    }

    /**
     * Converts Flight entity to FlightResponse DTO.
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
