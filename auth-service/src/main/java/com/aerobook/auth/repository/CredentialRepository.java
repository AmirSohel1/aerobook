package com.aerobook.auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aerobook.auth.entity.Credential;

/**
 * ============================================================================
 * Spring Data JPA Repository: CredentialRepository
 * ============================================================================
 *
 * Provides database access operations for {@link Credential} entities. Supports
 * email lookups, duplicate existence checks, and user-service ID
 * cross-referencing.
 *
 * @author Aerobook Security Engineering
 * @version 1.0.0
 */
@Repository
public interface CredentialRepository extends JpaRepository<Credential, Long> {

    /**
     * Finds credential record matching registered email address.
     *
     * @param email account email
     * @return {@link Optional} containing matching {@link Credential} if
     * present
     */
    Optional<Credential> findByEmail(String email);

    /**
     * Checks whether an account with the specified email already exists.
     *
     * @param email account email
     * @return {@code true} if an account exists; {@code false} otherwise
     */
    boolean existsByEmail(String email);

    /**
     * Finds credential record linked to a specific user-service profile ID.
     *
     * @param userId user account database identifier
     * @return {@link Optional} containing matching {@link Credential} if
     * present
     */
    Optional<Credential> findByUserId(Long userId);
}
