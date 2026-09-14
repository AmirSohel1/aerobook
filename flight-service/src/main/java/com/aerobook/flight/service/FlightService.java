package com.aerobook.flight.service;

import java.util.List;

import com.aerobook.flight.dto.request.CreateFlightRequest;
import com.aerobook.flight.dto.request.UpdateFlightRequest;
import com.aerobook.flight.dto.response.FlightResponse;

/**
 * Service interface for flight management operations.
 */
public interface FlightService {

    /**
     * Creates a new flight.
     *
     * @param request flight creation request
     * @return created flight details
     */
    FlightResponse createFlight(CreateFlightRequest request);

    /**
     * Updates an existing flight.
     *
     * @param id flight id
     * @param request updated flight information
     * @return updated flight details
     */
    FlightResponse updateFlight(Long id, UpdateFlightRequest request);

    /**
     * Deletes a flight.
     *
     * @param id flight id
     */
    void deleteFlight(Long id);

    /**
     * Fetches flight by id.
     *
     * @param id flight id
     * @return flight details
     */
    FlightResponse getFlightById(Long id);

    /**
     * Fetches all available flights.
     *
     * @return list of flights
     */
    List<FlightResponse> getAllFlights();
}