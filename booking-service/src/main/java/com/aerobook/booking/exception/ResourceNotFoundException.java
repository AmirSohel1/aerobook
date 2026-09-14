package com.aerobook.booking.exception;


/**
 * Exception thrown when a requested booking or associated entity cannot be found in the database.
 * Maps to HTTP 404 Not Found in {@link GlobalExceptionHandler}.
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Constructs a new {@link ResourceNotFoundException} with the specified error message.
     *
     * @param message descriptive explanation of the missing resource
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
}