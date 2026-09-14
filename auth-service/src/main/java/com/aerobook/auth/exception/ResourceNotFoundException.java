package com.aerobook.auth.exception;

/**
 * ============================================================================
 * Exception: ResourceNotFoundException
 * ============================================================================
 *
 * Thrown when an expected entity (user profile, credential, or refresh token)
 * cannot be located in the database. Mapped to HTTP 404 Not Found by
 * {@link GlobalExceptionHandler}.
 *
 * @author Aerobook Security Engineering
 * @version 1.0.0
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Constructs ResourceNotFoundException with descriptive error message.
     *
     * @param message error explanation
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
