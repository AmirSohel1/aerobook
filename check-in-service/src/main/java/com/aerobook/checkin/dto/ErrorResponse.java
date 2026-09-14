package com.aerobook.checkin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * ============================================================================
 * Standard API Error Response DTO
 * ============================================================================
 *
 * Uniform error envelope returned for airport check-in failures, duplicate
 * check-in attempts, and invalid payload formats.
 *
 * @author Aerobook Platform Engineering
 * @version 1.0.0
 */
@Schema(description = "Standard API error response payload")
public class ErrorResponse {

    /**
     * ISO-8601 timestamp marking the instant the error occurred.
     */
    @Schema(description = "Time when error occurred", example = "2026-09-14T14:30:00")
    private LocalDateTime timestamp;

    /**
     * HTTP response numeric status code.
     */
    @Schema(description = "HTTP status code", example = "400")
    private int status;

    /**
     * Standard HTTP reason phrase.
     */
    @Schema(description = "Error reason phrase", example = "Bad Request")
    private String error;

    /**
     * Human-readable diagnostic explanation of the failure.
     */
    @Schema(description = "Detailed error explanation message", example = "Check-in not found for booking ID: 1")
    private String message;

    /**
     * Field-level constraint violation mappings.
     */
    @Schema(description = "Optional validation errors mapped by field name")
    private Map<String, String> fieldErrors;

    /**
     * Default no-argument constructor.
     */
    public ErrorResponse() {
    }

    /**
     * Basic constructor for errors without field validations.
     *
     * @param timestamp timestamp
     * @param status status code
     * @param error error phrase
     * @param message error message
     */
    public ErrorResponse(LocalDateTime timestamp, int status, String error, String message) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.message = message;
    }

    /**
     * Constructor for validation errors including field-specific messages.
     *
     * @param timestamp timestamp
     * @param status status code
     * @param error error phrase
     * @param message error message
     * @param fieldErrors field error mappings
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
