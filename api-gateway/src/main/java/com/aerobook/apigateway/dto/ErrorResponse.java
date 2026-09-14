package com.aerobook.apigateway.dto;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * ============================================================================
 * Standard API Gateway Error Response Data Transfer Object
 * ============================================================================
 *
 * Uniform JSON error payload returned by the Gateway filters and global
 * exception handler when requests are rejected (e.g. 401 Unauthorized, 403
 * Forbidden, 500 Internal Server Error).
 *
 * @author Aerobook Platform Engineering
 * @version 1.0.0
 */
@Schema(description = "Standard API Gateway error response payload")
public class ErrorResponse {

    /**
     * Exact timestamp when the error or rejection occurred.
     */
    @Schema(description = "Time when error occurred", example = "2026-09-14T14:30:00")
    private LocalDateTime timestamp;

    /**
     * Standard HTTP response status code (e.g. 401, 403, 500).
     */
    @Schema(description = "HTTP status code", example = "403")
    private int status;

    /**
     * HTTP reason phrase corresponding to the status code.
     */
    @Schema(description = "Error reason phrase", example = "Forbidden")
    private String error;

    /**
     * Detailed human-readable explanation of why the request failed.
     */
    @Schema(description = "Detailed error explanation message", example = "Access denied: Requires ROLE_ADMIN authority")
    private String message;

    /**
     * Default no-args constructor required for Jackson JSON serialization.
     */
    public ErrorResponse() {
    }

    /**
     * Fully parameterized constructor to instantiate an error response.
     *
     * @param timestamp timestamp of the error
     * @param status HTTP status code
     * @param error HTTP reason phrase
     * @param message detailed descriptive error message
     */
    public ErrorResponse(LocalDateTime timestamp, int status, String error, String message) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.message = message;
    }

    /**
     * Gets the error occurrence timestamp.
     *
     * @return LocalDateTime error timestamp
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the error occurrence timestamp.
     *
     * @param timestamp the error timestamp
     */
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Gets the HTTP status code.
     *
     * @return integer HTTP status
     */
    public int getStatus() {
        return status;
    }

    /**
     * Sets the HTTP status code.
     *
     * @param status the HTTP status code
     */
    public void setStatus(int status) {
        this.status = status;
    }

    /**
     * Gets the HTTP reason phrase.
     *
     * @return String reason phrase
     */
    public String getError() {
        return error;
    }

    /**
     * Sets the HTTP reason phrase.
     *
     * @param error the reason phrase
     */
    public void setError(String error) {
        this.error = error;
    }

    /**
     * Gets the descriptive error message.
     *
     * @return String detailed message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the descriptive error message.
     *
     * @param message the detailed message
     */
    public void setMessage(String message) {
        this.message = message;
    }
}
