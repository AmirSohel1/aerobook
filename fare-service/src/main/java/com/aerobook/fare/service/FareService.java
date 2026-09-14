package com.aerobook.fare.service;

import java.util.List;

import com.aerobook.fare.dto.request.FareRequest;
import com.aerobook.fare.dto.response.FareResponse;
import com.aerobook.fare.exception.ResourceNotFoundException;

/**
 * ============================================================================
 * Fare & Revenue Management Business Service Interface
 * ============================================================================
 *
 * Core service contract defining lifecycle routines for tiered cabin fares,
 * pricing recalculations, and flight pricing audits.
 *
 * @author Aerobook Platform Engineering
 * @version 1.0.0
 */
public interface FareService {

    /**
     * Creates and persists a new tiered fare pricing structure for a flight
     * schedule.
     *
     * @param request validated fare creation payload
     * @return created {@link FareResponse} projection
     * @throws IllegalArgumentException if fare or percentage constraints are
     * violated
     */
    FareResponse createFare(FareRequest request);

    /**
     * Modifies an existing flight fare structure by primary fare ID.
     *
     * @param id fare record database ID
     * @param request updated pricing coordinates
     * @return updated {@link FareResponse} projection
     * @throws ResourceNotFoundException if fare ID does not exist
     * @throws IllegalArgumentException if pricing values fail business rules
     */
    FareResponse updateFare(Long id, FareRequest request);

    /**
     * Retrieves fare structure by primary fare record ID.
     *
     * @param id unique fare database identifier
     * @return matched {@link FareResponse}
     * @throws ResourceNotFoundException if not found
     */
    FareResponse getFareById(Long id);

    /**
     * Retrieves active fare pricing for a specific flight schedule. Consumed by
     * Booking Service during reservation cost calculation.
     *
     * @param flightId flight database identifier
     * @return active {@link FareResponse}
     * @throws ResourceNotFoundException if no fare is configured for the flight
     */
    FareResponse getFareByFlightId(Long flightId);

    /**
     * Retrieves all fare structures configured across the airline fleet.
     * Intended for administrative revenue management oversight.
     *
     * @return list of all {@link FareResponse} pricing schedules
     */
    List<FareResponse> getAllFares();

    /**
     * Permanently deletes a flight fare pricing structure.
     *
     * @param id unique fare database ID to delete
     * @throws ResourceNotFoundException if fare record does not exist
     */
    void deleteFare(Long id);
}
