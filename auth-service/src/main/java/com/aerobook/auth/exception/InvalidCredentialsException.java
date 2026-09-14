package com.aerobook.auth.exception;

/**
 * ============================================================================
 * Exception: InvalidCredentialsException
 * ============================================================================
 *
 * Thrown when credentials fail authentication (e.g. wrong password, duplicate
 * email, expired refresh token). Mapped to HTTP 401 Unauthorized by
 * {@link GlobalExceptionHandler}.
 *
 * @author Aerobook Security Engineering
 * @version 1.0.0
 */
public class InvalidCredentialsException extends RuntimeException {

    /**
     * Constructs InvalidCredentialsException with descriptive error message.
     *
     * @param message error explanation
     */
    public InvalidCredentialsException(String message) {
        super(message);
    }
}
