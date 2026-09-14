package com.aerobook.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * ============================================================================
 * Refresh Token Exchange Request Data Transfer Object
 * ============================================================================
 *
 * Submitted to {@code /api/auth/refresh-token} to exchange an active refresh
 * token for a new 1-hour JWT bearer access token.
 *
 * @author Aerobook Security Engineering
 * @version 1.0.0
 */
@Schema(description = "Request payload to refresh expired access token")
public class RefreshTokenRequest {

    /**
     * Active UUID refresh token string.
     */
    @Schema(description = "Valid unexpired refresh token string", example = "d9a3b64c-8f4b-4890-a241-119c67bc2f20")
    @NotBlank(message = "Refresh token is required")
    private String refreshToken;

    /**
     * Default constructor for JSON deserialization.
     */
    public RefreshTokenRequest() {
    }

    /**
     * Parameterized constructor.
     *
     * @param refreshToken active refresh token string
     */
    public RefreshTokenRequest(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
