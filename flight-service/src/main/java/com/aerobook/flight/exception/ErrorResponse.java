package com.aerobook.flight.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Standard error response returned to the client whenever an exception occurs.
 */
@Schema(description = "Standard API error response payload")
public class ErrorResponse {

    /**
     * Timestamp indicating when the error occurred.
     */
    @Schema(description = "Time at which the error occurred", example = "2026-09-14T14:30:00")
    private LocalDateTime timestamp;

    /**
     * HTTP response status code.
     */
    @Schema(description = "HTTP status code", example = "400")
    private int status;

    /**
     * HTTP reason phrase.
     */
    @Schema(description = "Error reason phrase", example = "Bad Request")
    private String error;

    /**
     * Human-readable diagnostic error message.
     */
    @Schema(description = "Detailed error explanation message", example = "Flight not found with given ID")
    private String message;

    /**
     * Map of bean validation errors keyed by field name.
     */
    @Schema(description = "Optional validation errors mapped by field name")
    private Map<String, String> fieldErrors;

    /**
     * Default no-args constructor for JSON serialization.
     */
    public ErrorResponse() {
    }

    /**
     * Constructs a basic {@link ErrorResponse} with timestamp and message.
     *
     * @param timestamp time of failure
     * @param message failure description
     */
    public ErrorResponse(LocalDateTime timestamp, String message) {
        this.timestamp = timestamp;
        this.message = message;
    }

    /**
     * Constructs an {@link ErrorResponse} with HTTP code, reason phrase, and
     * message.
     *
     * @param timestamp time of failure
     * @param status HTTP status integer
     * @param error HTTP reason phrase
     * @param message failure description
     */
    public ErrorResponse(LocalDateTime timestamp, int status, String error, String message) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.message = message;
    }

    /**
     * Constructs an {@link ErrorResponse} with bean validation field-specific
     * errors.
     *
     * @param timestamp time of failure
     * @param status HTTP status integer
     * @param error HTTP reason phrase
     * @param message failure description
     * @param fieldErrors map of field error messages
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
