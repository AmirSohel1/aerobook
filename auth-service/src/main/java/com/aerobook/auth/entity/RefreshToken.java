package com.aerobook.auth.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;

/**
 * ============================================================================
 * JPA Entity: RefreshToken
 * ============================================================================
 *
 * Persists persistent refresh tokens in MySQL table {@code refresh_tokens}.
 * Allows seamless access token renewal without requiring re-entry of user
 * passwords.
 *
 * @author Aerobook Security Engineering
 * @version 1.0.0
 */
@Entity
@Table(name = "refresh_tokens")
public class RefreshToken {

    /**
     * Primary auto-increment identifier for the refresh token record.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Unique UUID token string.
     */
    @Column(nullable = false, unique = true)
    private String token;

    /**
     * Expiry timestamp (typically 7 days after issuance).
     */
    @Column(nullable = false)
    private LocalDateTime expiryDate;

    /**
     * Associated credential record ID linking this token to a user account.
     */
    @Column(nullable = false)
    private Long credentialId;

    public RefreshToken() {
    }

    public RefreshToken(Long id,
            String token,
            LocalDateTime expiryDate,
            Long credential) {
        this.id = id;
        this.token = token;
        this.expiryDate = expiryDate;
        this.credentialId = credential;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }

    public Long getCredentialId() {
        return credentialId;
    }

    public void setCredentialId(Long credential) {
        this.credentialId = credential;
    }
}
