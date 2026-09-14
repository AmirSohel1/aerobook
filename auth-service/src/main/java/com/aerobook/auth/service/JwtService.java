package com.aerobook.auth.service;

/**
 * Service contract for JSON Web Token (JWT) generation, validation, and claims
 * extraction.
 *
 * <p>
 * Employs HMAC-SHA256 signature algorithms using a configurable secret key to
 * issue stateless bearer tokens containing subject email and authority
 * roles.</p>
 *
 * @author AeroBook Development Team
 * @version 1.0
 */
public interface JwtService {

    /**
     * Generates a signed JWT access token embedding the user's email subject
     * and granted role claim.
     *
     * @param email the user's unique email address serving as token subject
     * @param role the user's authorization role (e.g., "ROLE_USER" or
     * "ROLE_ADMIN")
     * @return signed JWT compact string representation
     */
    String generateToken(String email, String role);

    /**
     * Extracts the subject username/email from a signed JWT string.
     *
     * @param token the signed JWT compact token
     * @return the subject email encapsulated within the token payload
     */
    String extractUsername(String token);

    /**
     * Validates whether the given JWT token is cryptographically authentic,
     * untampered, and unexpired.
     *
     * @param token the signed JWT string to check
     * @return {@code true} if signature verification passes and token is not
     * expired, {@code false} otherwise
     */
    boolean validateToken(String token);
}
