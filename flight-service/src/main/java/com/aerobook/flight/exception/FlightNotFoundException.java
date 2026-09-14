package com.aerobook.flight.exception;

/**
 * Exception thrown when the requested flight
 * is not found in the database.
 */
public class FlightNotFoundException extends RuntimeException {

    public FlightNotFoundException(String message) {
        super(message);
    }
}