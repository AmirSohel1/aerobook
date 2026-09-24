package com.aerobook.flight.enums;

/**
 * Operational lifecycle states of a scheduled commercial flight.
 */
public enum FlightStatus {

    /**
     * Flight is scheduled on calendar and available for customer bookings.
     */
    SCHEDULED,
    /**
     * Flight is delayed due to weather, mechanical, or air traffic control
     * reasons.
     */
    DELAYED,
    /**
     * Flight has been cancelled; new bookings are rejected.
     */
    CANCELLED,
    /**
     * Flight is currently boarding passengers at airport gate.
     */
    BOARDING,
    /**
     * Flight has pushed back from gate and departed.
     */
    DEPARTED,
    /**
     * Flight has completed its route and arrived at destination.
     */
    COMPLETED
}
