package com.aerobook.user.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.aerobook.user.dto.response.ErrorResponse;

/**
 * ============================================================================
 * Centralized REST Exception Handler Advice
 * ============================================================================
 *
 * Intercepts uncaught controller exceptions across User Service, translating
 * them into standardized {@link ErrorResponse} JSON payloads with correct HTTP
 * status codes.
 *
 * <ul>
 * <li>{@link ResourceNotFoundException} $\rightarrow$ HTTP 404 Not Found</li>
 * <li>{@link IllegalArgumentException} $\rightarrow$ HTTP 409 Conflict
 * (duplicates) or HTTP 400 Bad Request</li>
 * <li>{@link MethodArgumentNotValidException} $\rightarrow$ HTTP 400 Bad
 * Request with field errors</li>
 * <li>{@link Exception} $\rightarrow$ HTTP 500 Internal Server Error</li>
 * </ul>
 *
 * @author Aerobook Platform Engineering
 * @version 1.0.0
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Intercepts missing entity lookups and constructs an HTTP 404 response.
     *
     * @param ex intercepted {@link ResourceNotFoundException}
     * @return {@link ResponseEntity} wrapping the error model
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                ex.getMessage()
        );
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    /**
     * Intercepts invalid business arguments or duplicate email/phone conflicts.
     *
     * @param ex intercepted {@link IllegalArgumentException}
     * @return {@link ResponseEntity} with HTTP 409 (if duplicate) or HTTP 400
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        // Classify duplicate conflicts as 409 Conflict rather than generic 400
        HttpStatus status = ex.getMessage() != null && ex.getMessage().contains("already exists")
                ? HttpStatus.CONFLICT
                : HttpStatus.BAD_REQUEST;

        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                ex.getMessage()
        );
        return new ResponseEntity<>(error, status);
    }

    /**
     * Intercepts Jakarta Bean Validation errors and extracts field-level
     * messages.
     *
     * @param ex intercepted {@link MethodArgumentNotValidException}
     * @return {@link ResponseEntity} with HTTP 400 and mapped field errors
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(fe.getField(), fe.getDefaultMessage());
        }

        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "Validation failed for input payload",
                fieldErrors
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * Fallback catch-all handler for unanticipated runtime exceptions.
     *
     * @param ex generic intercepted {@link Exception}
     * @return {@link ResponseEntity} with HTTP 500 Internal Server Error
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex) {
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                ex.getMessage()
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
