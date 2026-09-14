package com.aerobook.apigateway.util;

import java.nio.charset.StandardCharsets;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

/**
 * ============================================================================
 * Cryptographic JWT Token Verification and Claims Extraction Utility
 * ============================================================================
 *
 * Provides cryptographic HMAC-SHA signature verification and claims extraction
 * for JWT bearer tokens using the modern JJWT 0.12+ API.
 *
 * <p>
 * Key Responsibilities:
 * <ul>
 * <li>Converts configured secret key ({@code jwt.secret}) into a secure
 * {@link SecretKey}.</li>
 * <li>Validates token integrity, cryptographic signature, and unexpired
 * validity.</li>
 * <li>Extracts authenticated subject (username/email) and custom claims
 * (security role).</li>
 * </ul>
 *
 * @author Aerobook Platform Engineering
 * @version 1.0.0
 */
@Component
public class JwtUtil {

    /**
     * Secret key for signing and verifying tokens, injected from application
     * properties.
     */
    @Value("${jwt.secret}")
    private String secret;

    /**
     * Converts the raw string secret into an HMAC-SHA cryptographic SecretKey.
     *
     * @return {@link SecretKey} suitable for HMAC signing and verification
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8)
        );
    }

    /**
     * Cryptographically verifies the signature and parses all claims contained
     * within the signed JWT bearer token.
     *
     * @param token the encoded JWT string
     * @return parsed {@link Claims} payload
     * @throws io.jsonwebtoken.JwtException if the token is forged, tampered
     * with, or expired
     */
    public Claims extractAllClaims(String token) {

        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Extracts the subject (registered user email address) from the token
     * claims.
     *
     * @param token the encoded JWT string
     * @return String email username
     */
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    /**
     * Validates whether a token has a valid signature, proper format, and is
     * unexpired.
     *
     * @param token the encoded JWT string to validate
     * @return {@code true} if valid and unexpired; {@code false} if invalid or
     * expired
     */
    public boolean validateToken(String token) {

        try {
            extractAllClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
