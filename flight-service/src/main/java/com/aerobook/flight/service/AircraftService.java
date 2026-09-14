package com.aerobook.flight.service;

import java.util.List;

import com.aerobook.flight.dto.request.CreateAircraftRequest;
import com.aerobook.flight.entity.Aircraft;

/**
 * Service interface for managing aircraft operations.
 */
public interface AircraftService {

    /**
     * Creates a new aircraft.
     *
     * @param request aircraft creation request
     * @return created aircraft
     */
    Aircraft createAircraft(CreateAircraftRequest request);

    /**
     * Retrieves aircraft details by id.
     *
     * @param id aircraft id
     * @return aircraft details
     */
    Aircraft getAircraft(Long id);

    /**
     * Retrieves all aircrafts available in the system.
     *
     * @return list of aircrafts
     */
    List<Aircraft> getAllAircrafts();

    /**
     * Deletes an aircraft using its unique identifier.
     *
     * @param id aircraft id
     */
    void deleteAircraft(Long id);
}