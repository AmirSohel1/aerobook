package com.aerobook.apigateway.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.aerobook.apigateway.dto.ErrorResponse;

/**
 * ============================================================================
 * Centralized Global Exception Handler for API Gateway
 * ============================================================================
 *
 * Catches unhandled runtime exceptions, authentication rejections, and
 * downstream connectivity failures across the reactive WebFlux gateway
 * controller layer, producing standardized {@link ErrorResponse} JSON payloads.
 *
 * @author Aerobook Platform Engineering
 * @version 1.0.0
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Intercepts unchecked {@link RuntimeException} occurrences (such as
     * security token parse errors, missing claims, or authentication
     * rejections) and maps them to an HTTP 401 Unauthorized status.
     *
     * @param ex the intercepted RuntimeException
     * @return {@link ResponseEntity} wrapping an {@link ErrorResponse} with 401
     * status
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex) {
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.UNAUTHORIZED.value(),
                HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                ex.getMessage() != null ? ex.getMessage() : "Unauthorized access"
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    /**
     * Fallback handler for all unexpected or unhandled general
     * {@link Exception} instances. Maps them to an HTTP 500 Internal Server
     * Error status.
     *
     * @param ex the intercepted Exception
     * @return {@link ResponseEntity} wrapping an {@link ErrorResponse} with 500
     * status
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex) {
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                ex.getMessage() != null ? ex.getMessage() : "An unexpected gateway error occurred"
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
