package com.aerobook.checkin.exception;

import com.aerobook.checkin.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * ============================================================================
 * Check-In Service Centralized Exception Handler Advice
 * ============================================================================
 *
 * Catches unhandled exceptions across check-in controllers and maps them into
 * uniform {@link ErrorResponse} JSON structures with accurate HTTP status codes.
 *
 * @author Aerobook Platform Engineering
 * @version 1.0.0
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles Spring {@link ResponseStatusException} (such as 404 Not Found or 400 Bad Request).
     *
     * @param ex intercepted {@link ResponseStatusException}
     * @return {@link ResponseEntity} wrapping the structured error response
     */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatusException(ResponseStatusException ex) {
        int status = ex.getStatusCode().value();
        HttpStatus httpStatus = HttpStatus.resolve(status) != null ? HttpStatus.resolve(status) : HttpStatus.BAD_REQUEST;
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                status,
                httpStatus.getReasonPhrase(),
                ex.getReason() != null ? ex.getReason() : ex.getMessage()
        );
        return new ResponseEntity<>(error, httpStatus);
    }

    /**
     * Handles business logic argument violations.
     *
     * @param ex intercepted {@link IllegalArgumentException}
     * @return {@link ResponseEntity} with HTTP 400 Bad Request
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                ex.getMessage()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * Catches Jakarta Bean Validation constraint violations on incoming DTOs.
     *
     * @param ex intercepted {@link MethodArgumentNotValidException}
     * @return {@link ResponseEntity} with HTTP 400 and mapped field errors
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
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
     * Catch-all fallback for unexpected server errors.
     *
     * @param ex general caught {@link Exception}
     * @return {@link ResponseEntity} with HTTP 500 Internal Server Error
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex) {
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                ex.getMessage() != null ? ex.getMessage() : "An unexpected server error occurred"
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
