package com.aerobook.fare.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * ============================================================================
 * Standard API Error Response DTO
 * ============================================================================
 *
 * Uniform error envelope returned for fare validation failures, missing
 * flight pricing records, or downstream service exceptions.
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
     * Standard HTTP canonical reason phrase.
     */
    @Schema(description = "Error reason phrase", example = "Bad Request")
    private String error;

    /**
     * Diagnostic error explanation message.
     */
    @Schema(description = "Detailed error explanation message", example = "Economy fare cannot be negative.")
    private String message;

    /**
     * Field-level validation violations.
     */
    @Schema(description = "Optional validation errors mapped by field name")
    private Map<String, String> fieldErrors;

    /**
     * Default no-args constructor.
     */
    public ErrorResponse() {
    }

    /**
     * Constructor for errors without field violation mappings.
     *
     * @param timestamp timestamp
     * @param status status code
     * @param error reason phrase
     * @param message error message
     */
    public ErrorResponse(LocalDateTime timestamp, int status, String error, String message) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.message = message;
    }

    /**
     * Constructor for validation errors including field-specific violations.
     *
     * @param timestamp timestamp
     * @param status status code
     * @param error reason phrase
     * @param message error message
     * @param fieldErrors field-level violation mapping
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
