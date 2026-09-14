package com.aerobook.flight.service;

/**
 * Service interface for seat reservation management.
 *
 * This service is used by Booking Service to reserve
 * and release seats during booking operations.
 */
public interface SeatManagementService {

    /**
     * Reserve one seat for a flight.
     *
     * @param flightId flight identifier
     */
    void reserveSeat(Long flightId);

    /**
     * Release one seat back to inventory.
     *
     * @param flightId flight identifier
     */
    void releaseSeat(Long flightId);
}