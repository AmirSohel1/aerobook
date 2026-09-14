package com.aerobook.auth.service;

import com.aerobook.auth.entity.RefreshToken;

/**
 * Service contract for persistent refresh token management.
 *
 * <p>
 * Handles issuing long-lived random UUID refresh tokens, validating expiration,
 * and revoking tokens upon logout or role changes.</p>
 *
 * @author AeroBook Development Team
 * @version 1.0
 */
public interface RefreshTokenService {

    /**
     * Creates and persists a new 7-day refresh token for the specified
     * credential record.
     *
     * @param credentialId the primary key of the credential associated with the
     * refresh token
     * @return the newly persisted {@link RefreshToken} entity
     */
    RefreshToken createRefreshToken(Long credentialId);

    /**
     * Verifies the authenticity and expiration status of a supplied refresh
     * token.
     *
     * <p>
     * If the token has expired, it is deleted from the database and an
     * exception is raised.</p>
     *
     * @param token the opaque UUID refresh token string to inspect
     * @return the valid {@link RefreshToken} entity
     * @throws com.aerobook.auth.exception.ResourceNotFoundException if the
     * token does not exist
     * @throws RuntimeException if the token has expired past its 7-day validity
     * window
     */
    RefreshToken verifyRefreshToken(String token);

    /**
     * Revokes and removes all refresh tokens associated with a given credential
     * identifier.
     *
     * @param credentialId the primary key of the credential whose tokens should
     * be purged
     */
    void deleteByCredentialId(Long credentialId);
}
