package com.aerobook.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * ============================================================================
 * Standard Auth Service Error Response Data Transfer Object
 * ============================================================================
 *
 * Uniform error payload returned across all authentication and validation
 * failures.
 *
 * @author Aerobook Security Engineering
 * @version 1.0.0
 */
@Schema(description = "Standard API error response payload")
public class ErrorResponse {

    /**
     * Exact timestamp when the error occurred.
     */
    @Schema(description = "Time when error occurred", example = "2026-09-14T14:30:00")
    private LocalDateTime timestamp;

    /**
     * HTTP status code (e.g. 400, 401, 403, 404, 409).
     */
    @Schema(description = "HTTP status code", example = "401")
    private int status;

    /**
     * HTTP status reason phrase.
     */
    @Schema(description = "Error reason phrase", example = "Unauthorized")
    private String error;

    /**
     * Human-readable detailed explanation of error.
     */
    @Schema(description = "Detailed error explanation message", example = "Invalid email or password credentials")
    private String message;

    /**
     * Optional map of field names to specific validation error messages.
     */
    @Schema(description = "Optional validation errors mapped by field name")
    private Map<String, String> fieldErrors;

    /**
     * Default constructor for JSON deserialization.
     */
    public ErrorResponse() {
    }

    /**
     * Parameterized constructor without field errors.
     *
     * @param timestamp error timestamp
     * @param status HTTP status code
     * @param error HTTP reason phrase
     * @param message detailed message
     */
    public ErrorResponse(LocalDateTime timestamp, int status, String error, String message) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.message = message;
    }

    /**
     * Parameterized constructor including field-level validation errors.
     *
     * @param timestamp error timestamp
     * @param status HTTP status code
     * @param error HTTP reason phrase
     * @param message detailed message
     * @param fieldErrors map of field validation failures
     */
    public ErrorResponse(LocalDateTime timestamp, int status, String error, String message, Map<String, String> fieldErrors) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.message = message;
        this.fieldErrors = fieldErrors;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Map<String, String> getFieldErrors() {
        return fieldErrors;
    }

    public void setFieldErrors(Map<String, String> fieldErrors) {
        this.fieldErrors = fieldErrors;
    }
}
