package com.aerobook.auth.service.impl;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.aerobook.auth.service.JwtService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

/**
 * Implementation of {@link JwtService} employing HMAC-SHA256 (HS256) signatures
 * via JJWT.
 *
 * <p>
 * Handles:
 * <ul>
 * <li>Secret key conversion to {@link SecretKey} specification</li>
 * <li>Token construction embedding user email subject, role claims, issue
 * timestamp, and expiration</li>
 * <li>Claims parsing, cryptographic signature verification, and subject
 * extraction</li>
 * <li>Tamper detection and token expiration validation</li>
 * </ul>
 * </p>
 *
 * @author AeroBook Development Team
 * @version 1.0
 */
@Service
public class JwtServiceImpl implements JwtService {

    /**
     * Base64 or UTF-8 encoded secret key used for signing and verifying
     * HMAC-SHA tokens.
     */
    @Value("${jwt.secret}")
    private String secretKey;

    /**
     * Access token time-to-live (TTL) duration in milliseconds.
     */
    @Value("${jwt.expiration}")
    private long jwtExpiration;

    /**
     * Converts the configured string secret key into a cryptographic HMAC-SHA
     * {@link SecretKey}.
     *
     * @return initialized {@link SecretKey} for token signing and verification
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(
                secretKey.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * Builds a compact serialized JWT containing:
     * <ul>
     * <li>{@code sub}: user email</li>
     * <li>{@code role}: granted authority string (e.g. ROLE_USER,
     * ROLE_ADMIN)</li>
     * <li>{@code iat}: current system timestamp</li>
     * <li>{@code exp}: current timestamp + configured TTL</li>
     * </ul>
     * </p>
     */
    @Override
    public String generateToken(String email, String role) {

        Date issuedAt = new Date();

        Date expiration = new Date(
                issuedAt.getTime() + jwtExpiration);

        // Assemble, sign with HMAC-SHA key, and compact into serialized string
        return Jwts.builder()
                .subject(email)
                .claim("role", role)
                .issuedAt(issuedAt)
                .expiration(expiration)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * Parses the signed JWT payload claims and retrieves the subject field.</p>
     */
    @Override
    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * Attempts to parse and verify the JWT signature. Returns {@code true} if
     * authentic and unexpired; returns {@code false} if malformed, expired, or
     * tampered with.</p>
     */
    @Override
    public boolean validateToken(String token) {

        try {
            extractClaims(token);
            return true;
        } catch (Exception e) {
            // Signature verification failed, token is expired, or format is malformed
            return false;
        }
    }

    /**
     * Parses the signed JWT claims payload using JJWT parser with the
     * configured signing key.
     *
     * @param token the compact serialized JWT string
     * @return the extracted {@link Claims} claims body
     * @throws io.jsonwebtoken.JwtException if the token cannot be parsed or
     * verified
     */
    public Claims extractClaims(String token) {

        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
