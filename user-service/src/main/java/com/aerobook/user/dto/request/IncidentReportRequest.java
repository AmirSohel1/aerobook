package com.aerobook.user.dto.request;

import com.aerobook.user.entity.IncidentSeverity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Request payload for reporting a passenger or operational incident")
public class IncidentReportRequest {

    @Schema(description = "ID of passenger/user involved", example = "101")
    @NotNull(message = "User ID is required")
    private Long userId;

    @Schema(description = "Name of user involved", example = "John Doe")
    @NotBlank(message = "User name is required")
    private String userName;

    @Schema(description = "Email of user involved", example = "john@example.com")
    @NotBlank(message = "User email is required")
    private String userEmail;

    @Schema(description = "Staff or admin reporting the incident", example = "Staff member (EMP-401)")
    @NotBlank(message = "Reporter identifier is required")
    private String reportedBy;

    @Schema(description = "Reason or description of the incident", example = "Unruly passenger behavior at boarding gate")
    @NotBlank(message = "Reason is required")
    private String reason;

    @Schema(description = "Severity level: LOW, MEDIUM, HIGH, CRITICAL", example = "MEDIUM")
    private IncidentSeverity severity;

    @Schema(description = "Operational notes or resolution remarks", example = "Security alerted; passenger re-accommodated")
    private String notes;

    public IncidentReportRequest() {
    }

    public IncidentReportRequest(Long userId, String userName, String userEmail, String reportedBy,
                                 String reason, IncidentSeverity severity, String notes) {
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.reportedBy = reportedBy;
        this.reason = reason;
        this.severity = severity;
        this.notes = notes;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getReportedBy() {
        return reportedBy;
    }

    public void setReportedBy(String reportedBy) {
        this.reportedBy = reportedBy;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public IncidentSeverity getSeverity() {
        return severity;
    }

    public void setSeverity(IncidentSeverity severity) {
        this.severity = severity;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
