package com.aerobook.auth.service.impl;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.aerobook.auth.entity.RefreshToken;
import com.aerobook.auth.exception.ResourceNotFoundException;
import com.aerobook.auth.repository.RefreshTokenRepository;
import com.aerobook.auth.service.RefreshTokenService;

/**
 * Implementation of {@link RefreshTokenService} managing long-lived refresh
 * tokens.
 *
 * <p>
 * Persists cryptographically random UUID tokens in MySQL with an expiration
 * timestamp set 7 days into the future. Enforces strict revocation and
 * expiration checks on every refresh.</p>
 *
 * @author AeroBook Development Team
 * @version 1.0
 */
@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {

    /**
     * Repository for CRUD operations on refresh token entities.
     */
    private final RefreshTokenRepository refreshTokenRepository;

    /**
     * Constructs the refresh token service with required repository.
     *
     * @param refreshTokenRepository the refresh token repository
     */
    public RefreshTokenServiceImpl(
            RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * Generates a random UUID string, sets its expiry date to
     * {@code LocalDateTime.now().plusDays(7)}, associates it with the specified
     * credential ID, and stores it in the database.</p>
     */
    @Override
    public RefreshToken createRefreshToken(Long credentialId) {

        // Instantiate new token entity with a unique UUID string and 7-day validity
        RefreshToken refreshToken = new RefreshToken();

        refreshToken.setCredentialId(credentialId);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(
                LocalDateTime.now().plusDays(7));

        return refreshTokenRepository.save(refreshToken);
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * Finds the token by string value and verifies it has not expired. If
     * expired, removes the token record and throws a
     * {@link RuntimeException}.</p>
     */
    @Override
    public RefreshToken verifyRefreshToken(String token) {

        // Step 1: Look up refresh token in the repository
        RefreshToken refreshToken
                = refreshTokenRepository
                        .findByToken(token)
                        .orElseThrow(()
                                -> new ResourceNotFoundException(
                                "Refresh token not found"));

        // Step 2: Validate token expiry against current system timestamp
        if (refreshToken.getExpiryDate()
                .isBefore(LocalDateTime.now())) {

            // Purge expired token to prevent replay attacks
            refreshTokenRepository.delete(refreshToken);

            throw new RuntimeException(
                    "Refresh token expired");
        }

        return refreshToken;
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * Deletes all active refresh tokens mapped to the given credential
     * identifier, effectively logging the user out across sessions.</p>
     */
    @Override
    public void deleteByCredentialId(Long credentialId) {

        refreshTokenRepository
                .deleteByCredentialId(credentialId);
    }
}
