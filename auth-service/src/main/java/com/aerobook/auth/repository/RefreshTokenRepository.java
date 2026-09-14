package com.aerobook.auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aerobook.auth.entity.RefreshToken;

/**
 * ============================================================================
 * Spring Data JPA Repository: RefreshTokenRepository
 * ============================================================================
 *
 * Provides database access operations for {@link RefreshToken} entities.
 * Supports token string lookups and bulk revocation by credential ID.
 *
 * @author Aerobook Security Engineering
 * @version 1.0.0
 */
@Repository
public interface RefreshTokenRepository
        extends JpaRepository<RefreshToken, Long> {

    /**
     * Finds active refresh token record matching the provided UUID string.
     *
     * @param token refresh token UUID string
     * @return {@link Optional} containing matching {@link RefreshToken} if
     * present
     */
    Optional<RefreshToken> findByToken(String token);

    /**
     * Revokes and deletes all active refresh tokens associated with a user's
     * credential ID.
     *
     * @param credentialId target credential database identifier
     */
    void deleteByCredentialId(Long credentialId);
}
