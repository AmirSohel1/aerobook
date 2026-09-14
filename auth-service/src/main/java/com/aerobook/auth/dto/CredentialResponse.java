package com.aerobook.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

/**
 * ============================================================================
 * Credential Audit Response Data Transfer Object
 * ============================================================================
 *
 * Returned for administrative credential audits and role management operations.
 * Excludes sensitive BCrypt password hashes for security compliance.
 *
 * @author Aerobook Security Engineering
 * @version 1.0.0
 */
@Schema(description = "User credential details and assigned role")
public class CredentialResponse {

    /**
     * Primary credential database identifier in credentials table.
     */
    @Schema(description = "Primary credential database identifier", example = "1")
    private Long credentialId;

    /**
     * Associated customer account identifier in user-service.
     */
    @Schema(description = "Associated user account ID in user-service", example = "1")
    private Long userId;

    /**
     * Registered user email address used for login authentication.
     */
    @Schema(description = "Registered user email address", example = "asha.khan@example.com")
    private String email;

    /**
     * Active security role (ROLE_USER or ROLE_ADMIN).
     */
    @Schema(description = "Assigned security role (ROLE_USER or ROLE_ADMIN)", example = "ROLE_USER")
    private String role;

    /**
     * Account registration timestamp.
     */
    @Schema(description = "Account registration timestamp", example = "2026-10-01T08:00:00")
    private LocalDateTime createdAt;

    /**
     * Timestamp of last role update or credential modification.
     */
    @Schema(description = "Last update timestamp", example = "2026-10-01T08:00:00")
    private LocalDateTime updatedAt;

    /**
     * Default constructor for JSON deserialization.
     */
    public CredentialResponse() {
    }

    /**
     * Parameterized constructor.
     *
     * @param credentialId credential record ID
     * @param userId user account ID
     * @param email account email
     * @param role authority role name
     * @param createdAt registration timestamp
     * @param updatedAt modification timestamp
     */
    public CredentialResponse(Long credentialId, Long userId, String email, String role, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.credentialId = credentialId;
        this.userId = userId;
        this.email = email;
        this.role = role;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getCredentialId() {
        return credentialId;
    }

    public void setCredentialId(Long credentialId) {
        this.credentialId = credentialId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
