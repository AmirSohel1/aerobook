package com.aerobook.flight.service;

import java.util.List;

import com.aerobook.flight.dto.response.FlightResponse;

/**
 * Service interface for searching flights.
 *
 * This service provides operations to search available
 * flights based on source and destination locations.
 */
public interface FlightSearchService {

    /**
     * Searches flights based on source and destination.
     *
     * @param source departure location
     * @param destination arrival location
     * @return list of matching flights
     */
    List<FlightResponse> searchFlights(String source,
                                       String destination);
}