package com.aerobook.booking.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Standard error response model returned for booking validation failures and
 * exceptions across the Booking Service REST APIs.
 */
@Schema(description = "Standard API error response payload")
public class ErrorResponse {

    /**
     * Timestamp when the error was encountered.
     */
    @Schema(description = "Time when error occurred", example = "2026-09-14T14:30:00")
    private LocalDateTime timestamp;

    /**
     * HTTP status integer code (e.g., 400, 404, 500).
     */
    @Schema(description = "HTTP status code", example = "400")
    private int status;

    /**
     * Standard HTTP error phrase (e.g. Bad Request, Not Found).
     */
    @Schema(description = "Error reason phrase", example = "Bad Request")
    private String error;

    /**
     * Human-readable explanation of why the request failed.
     */
    @Schema(description = "Detailed error explanation message", example = "Cannot book flight: departure time has already passed.")
    private String message;

    /**
     * Detailed field validation error map (fieldName -> errorMessage).
     */
    @Schema(description = "Optional validation errors mapped by field name")
    private Map<String, String> fieldErrors;

    /**
     * Default no-args constructor for JSON serialization.
     */
    public ErrorResponse() {
    }

    /**
     * Constructs an {@link ErrorResponse} without field-specific errors.
     *
     * @param timestamp occurrence timestamp
     * @param status    HTTP status code
     * @param error     HTTP reason phrase
     * @param message   descriptive error message
     */
    public ErrorResponse(LocalDateTime timestamp, int status, String error, String message) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.message = message;
    }

    /**
     * Constructs an {@link ErrorResponse} with field-specific validation errors.
     *
     * @param timestamp   occurrence timestamp
     * @param status      HTTP status code
     * @param error       HTTP reason phrase
     * @param message     descriptive error message
     * @param fieldErrors map of field validation failure messages
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
