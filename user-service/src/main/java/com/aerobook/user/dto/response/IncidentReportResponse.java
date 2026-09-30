package com.aerobook.user.dto.response;

import java.time.LocalDateTime;

import com.aerobook.user.entity.IncidentSeverity;
import com.aerobook.user.entity.IncidentStatus;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response payload representing an incident record")
public class IncidentReportResponse {

    @Schema(description = "Incident primary key ID", example = "1")
    private Long id;

    @Schema(description = "User ID involved", example = "101")
    private Long userId;

    @Schema(description = "User full name", example = "John Doe")
    private String userName;

    @Schema(description = "User email address", example = "john@example.com")
    private String userEmail;

    @Schema(description = "Identifier of reporter", example = "Staff EMP-401")
    private String reportedBy;

    @Schema(description = "Reason or description of the incident", example = "Unruly passenger behavior at gate")
    private String reason;

    @Schema(description = "Severity level", example = "MEDIUM")
    private IncidentSeverity severity;

    @Schema(description = "Current lifecycle status", example = "OPEN")
    private IncidentStatus status;

    @Schema(description = "Operational notes", example = "Security alerted; passenger re-accommodated")
    private String notes;

    @Schema(description = "Creation timestamp")
    private LocalDateTime createdAt;

    public IncidentReportResponse() {
    }

    public IncidentReportResponse(Long id, Long userId, String userName, String userEmail,
                                  String reportedBy, String reason, IncidentSeverity severity,
                                  IncidentStatus status, String notes, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.reportedBy = reportedBy;
        this.reason = reason;
        this.severity = severity;
        this.status = status;
        this.notes = notes;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public IncidentStatus getStatus() {
        return status;
    }

    public void setStatus(IncidentStatus status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
