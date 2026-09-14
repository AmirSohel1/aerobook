package com.aerobook.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * ============================================================================
 * Standard API Error Response DTO
 * ============================================================================
 *
 * Uniform error envelope delivered whenever an HTTP request fails due to client
 * validation errors, duplicate resources, or system exceptions.
 *
 * @author Aerobook Platform Engineering
 * @version 1.0.0
 */
@Schema(description = "Standard API error response payload")
public class ErrorResponse {

    /**
     * ISO-8601 timestamp marking the instant the error was intercepted.
     */
    @Schema(description = "Timestamp when error occurred", example = "2026-09-14T14:30:00")
    private LocalDateTime timestamp;

    /**
     * Standard HTTP response status code (e.g. 400, 404, 409, 500).
     */
    @Schema(description = "HTTP status code", example = "400")
    private int status;

    /**
     * HTTP canonical reason phrase corresponding to the status code.
     */
    @Schema(description = "Error reason phrase", example = "Bad Request")
    private String error;

    /**
     * Human-readable diagnostic message explaining the cause of the failure.
     */
    @Schema(description = "Detailed error explanation message", example = "User with email ravi.patel@example.com already exists")
    private String message;

    /**
     * Optional key-value mapping of field names to validation constraint
     * violation messages.
     */
    @Schema(description = "Optional validation errors mapped by field name")
    private Map<String, String> fieldErrors;

    /**
     * Default no-argument constructor.
     */
    public ErrorResponse() {
    }

    /**
     * Constructor for simple error responses without field violations.
     *
     * @param timestamp failure timestamp
     * @param status HTTP numeric code
     * @param error HTTP reason phrase
     * @param message human-readable description
     */
    public ErrorResponse(LocalDateTime timestamp, int status, String error, String message) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.message = message;
    }

    /**
     * Constructor for complex validation error responses with field-specific
     * violations.
     *
     * @param timestamp failure timestamp
     * @param status HTTP numeric code
     * @param error HTTP reason phrase
     * @param message human-readable description
     * @param fieldErrors map of field name to violation reason
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
