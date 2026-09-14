package com.aerobook.auth.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;

/**
 * ============================================================================
 * JPA Entity: Credential
 * ============================================================================
 *
 * Persists security credentials for customer and administrator accounts in
 * MySQL table {@code credentials}. Manages BCrypt encrypted passwords and
 * security roles.
 *
 * @author Aerobook Security Engineering
 * @version 1.0.0
 */
@Entity
@Table(name = "credentials")
public class Credential {

    /**
     * Primary auto-increment identifier for the credential record.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long credentialId;

    /**
     * Foreign reference ID linking to user profile record in user-service.
     */
    @Column(nullable = false)
    private Long userId;

    /**
     * Unique email address used as username during login.
     */
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    /**
     * One-way cryptographic hash of password generated using BCrypt.
     */
    @Column(nullable = false)
    private String password;

    /**
     * Assigned security authority (ROLE_USER or ROLE_ADMIN).
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    /**
     * Creation timestamp automatically populated upon entity persist.
     */
    @Column(nullable = false)
    private LocalDateTime createdAt;

    /**
     * Modification timestamp updated whenever entity is updated.
     */
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public Credential() {
    }

    public Credential(Long credentialId,
            Long userId,
            String email,
            String password,
            Role role,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        this.credentialId = credentialId;
        this.userId = userId;
        this.email = email;
        this.password = password;
        this.role = role;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * Lifecycle callback setting creation and update timestamps before persist.
     */
    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Lifecycle callback refreshing update timestamp before update.
     */
    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
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
