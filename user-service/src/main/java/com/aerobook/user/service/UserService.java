package com.aerobook.user.service;

import java.util.List;

import com.aerobook.user.dto.request.UserRequest;
import com.aerobook.user.dto.response.UserResponse;
import com.aerobook.user.exception.ResourceNotFoundException;

/**
 * ============================================================================
 * User Profile Management Service Interface
 * ============================================================================
 *
 * Core business abstraction providing customer account lifecycle routines,
 * profile retrieval, administrative directory audits, and updates.
 *
 * @author Aerobook Platform Engineering
 * @version 1.0.0
 */
public interface UserService {

    /**
     * Creates and persists a new user profile record in the database. Enforces
     * unique constraint checks on email.
     *
     * @param request the customer registration coordinates
     * @return the created {@link UserResponse} projection
     * @throws IllegalArgumentException if the email is already registered
     */
    UserResponse createUser(UserRequest request);

    /**
     * Retrieves user profile coordinates by database primary ID.
     *
     * @param id the unique user database identifier
     * @return the matched {@link UserResponse} projection
     * @throws ResourceNotFoundException if no user exists with the given ID
     */
    UserResponse getUserById(Long id);

    /**
     * Retrieves user profile details by registered email address.
     *
     * @param email the user's electronic mail address
     * @return the matched {@link UserResponse} projection
     * @throws ResourceNotFoundException if no account matches the email
     */
    UserResponse getUserByEmail(String email);

    /**
     * Retrieves all customer user accounts stored in the platform database.
     * Intended for administrative directory oversight.
     *
     * @return list of all registered {@link UserResponse} entities
     */
    List<UserResponse> getAllUsers();

    /**
     * Modifies an existing customer user profile. Re-validates email uniqueness
     * if the email is changed.
     *
     * @param id the unique user database identifier
     * @param request the updated profile coordinates
     * @return the updated {@link UserResponse} projection
     * @throws ResourceNotFoundException if the user record does not exist
     * @throws IllegalArgumentException if the updated email is already taken or
     * role is invalid
     */
    UserResponse updateUser(Long id, UserRequest request);

    /**
     * Permanently purges a customer profile by its primary key.
     *
     * @param id the unique user identifier to remove
     * @throws ResourceNotFoundException if the user does not exist
     */
    void deleteUser(Long id);
}
