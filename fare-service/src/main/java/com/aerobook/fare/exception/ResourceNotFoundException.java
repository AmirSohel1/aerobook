package com.aerobook.fare.exception;

/**
 * ============================================================================
 * Fare Resource Not Found Exception
 * ============================================================================
 *
 * Thrown when a pricing structure requested by primary fare ID or associated
 * flight ID does not exist in the database. Maps to HTTP 404 Not Found.
 *
 * @author Aerobook Platform Engineering
 * @version 1.0.0
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Constructs the exception with an informative message.
     *
     * @param message failure detail message
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
