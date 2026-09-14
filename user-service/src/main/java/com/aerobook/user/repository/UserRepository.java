package com.aerobook.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aerobook.user.entity.UserEntity;

/**
 * ============================================================================
 * User Profile Spring Data JPA Repository
 * ============================================================================
 *
 * Data access abstraction managing CRUD operations, uniqueness queries, and
 * email searches on the {@link UserEntity} table in {@code aerobook_user_db}.
 *
 * @author Aerobook Platform Engineering
 * @version 1.0.0
 */
@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    /**
     * Looks up an active user account matching the exact email address.
     *
     * @param email unique email address to search
     * @return an {@link Optional} containing the matched {@link UserEntity}, or
     * empty if not found
     */
    Optional<UserEntity> findByEmail(String email);

    /**
     * Verifies whether an account has already been registered with the
     * specified email.
     *
     * @param email electronic mail address to check
     * @return {@code true} if a record exists, {@code false} otherwise
     */
    boolean existsByEmail(String email);

    /**
     * Verifies whether an account is already linked to the provided phone
     * number.
     *
     * @param phoneNumber contact phone string to check
     * @return {@code true} if already taken, {@code false} otherwise
     */
    boolean existsByPhoneNumber(String phoneNumber);
}
