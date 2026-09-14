package com.aerobook.flight.enums;

/**
 * Lifecycle states of an aircraft within the airline fleet.
 */
public enum AircraftStatus {

    /**
     * Aircraft is airworthy and operational for scheduled routes.
     */
    ACTIVE,
    /**
     * Aircraft is decommissioned or temporarily removed from service.
     */
    INACTIVE,
    /**
     * Aircraft is undergoing technical maintenance or inspection.
     */
    MAINTENANCE
}
