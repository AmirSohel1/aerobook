package com.aerobook.user.exception;

/**
 * ============================================================================
 * Resource Not Found Business Exception
 * ============================================================================
 *
 * Unchecked domain exception thrown when a requested user entity cannot be
 * located in the database by its primary key, email, or other criteria. Maps to
 * HTTP {@code 404 Not Found}.
 *
 * @author Aerobook Platform Engineering
 * @version 1.0.0
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Constructs the exception with a descriptive error message.
     *
     * @param msg error detail explanation
     */
    public ResourceNotFoundException(String msg) {
        super(msg);
    }
}
