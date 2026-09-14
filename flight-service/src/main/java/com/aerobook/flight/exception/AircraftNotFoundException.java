package com.aerobook.flight.exception;

/**
 * Exception thrown when the requested aircraft
 * is not found in the database.
 */
public class AircraftNotFoundException extends RuntimeException {

    public AircraftNotFoundException(String message) {
        super(message);
    }
}