package com.aerobook.auth.exception;

import com.aerobook.auth.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * ============================================================================
 * Central Global Exception Handler for Auth Service
 * ============================================================================
 *
 * Catches application-level exceptions and maps them into standardized
 * {@link ErrorResponse} JSON objects across all REST controllers.
 *
 * <p>
 * Handled Scenarios:
 * <ul>
 * <li>{@link ResourceNotFoundException} $\rightarrow$ HTTP 404 Not Found</li>
 * <li>{@link InvalidCredentialsException} $\rightarrow$ HTTP 401
 * Unauthorized</li>
 * <li>{@link IllegalArgumentException} $\rightarrow$ HTTP 400 Bad Request</li>
 * <li>{@link MethodArgumentNotValidException} $\rightarrow$ HTTP 400 Bad
 * Request with field-level map</li>
 * <li>{@link Exception} (General) $\rightarrow$ HTTP 500 Internal Server
 * Error</li>
 * </ul>
 *
 * @author Aerobook Security Engineering
 * @version 1.0.0
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles missing user or credential lookup exceptions.
     *
     * @param ex intercepted ResourceNotFoundException
     * @return 404 Not Found with error details
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                ex.getMessage()
        );
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles invalid password, expired token, or duplicate email conflicts.
     *
     * @param ex intercepted InvalidCredentialsException
     * @return 401 Unauthorized with error message
     */
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredentialsException(InvalidCredentialsException ex) {
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.UNAUTHORIZED.value(),
                HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                ex.getMessage()
        );
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    /**
     * Handles illegal argument exceptions (e.g. invalid role name during
     * promotion).
     *
     * @param ex intercepted IllegalArgumentException
     * @return 400 Bad Request
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                ex.getMessage()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles Jakarta validation annotation failures on request bodies (e.g.
     * @NotBlank, @Email). Populates a map of specific field names to error
     * messages.
     *
     * @param ex intercepted validation exception
     * @return 400 Bad Request with field-level validation errors
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Validation Failed",
                "One or more fields failed validation requirements.",
                fieldErrors
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * Global fallback for any unexpected system or database errors.
     *
     * @param ex generic Exception
     * @return 500 Internal Server Error
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex) {
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                ex.getMessage() != null ? ex.getMessage() : "An unexpected server error occurred"
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
