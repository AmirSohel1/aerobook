package com.aerobook.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * ============================================================================
 * Authentication Success Response Data Transfer Object
 * ============================================================================
 *
 * Returned upon successful customer registration, administrator setup, credential
 * login, or token refresh. Encapsulates signed JWT access and refresh tokens.
 *
 * @author Aerobook Security Engineering
 * @version 1.0.0
 */
@Schema(description = "Authentication response containing JWT tokens and account details")
public class AuthResponse {

    /**
     * Signed HMAC-SHA256 JWT access token (valid 1 hour).
     */
    @Schema(description = "JWT Bearer access token (valid 1 hour)", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String accessToken;

    /**
     * Unique UUID refresh token string stored in database (valid 7 days).
     */
    @Schema(description = "Refresh token string (valid 7 days)", example = "d9a3b64c-8f4b-4890-a241-119c67bc2f20")
    private String refreshToken;

    /**
     * Authorization scheme type identifier (default: "Bearer").
     */
    @Schema(description = "Token type identifier", example = "Bearer")
    private String tokenType;

    /**
     * Assigned user authority role (e.g. "ROLE_USER", "ROLE_ADMIN").
     */
    @Schema(description = "Assigned user authority role", example = "ROLE_USER")
    private String role;

    /**
     * Unique user account primary identifier in user-service.
     */
    @Schema(description = "Unique user identifier in the system", example = "1")
    private Long userId;

    /**
     * Default constructor for JSON deserialization.
     */
    public AuthResponse() {
    }

    /**
     * Parameterized constructor to initialize authentication response.
     *
     * @param accessToken signed JWT bearer access token
     * @param refreshToken persistent refresh token UUID
     * @param tokenType token prefix type (Bearer)
     * @param role security authority role
     * @param userId user account identifier
     */
    public AuthResponse(String accessToken,
            String refreshToken,
            String tokenType,
            String role,
            Long userId) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.tokenType = tokenType;
        this.role = role;
        this.userId = userId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
