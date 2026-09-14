package com.aerobook.auth.service;

import com.aerobook.auth.dto.AuthResponse;
import com.aerobook.auth.dto.CredentialResponse;
import com.aerobook.auth.dto.LoginRequest;
import com.aerobook.auth.dto.RefreshTokenRequest;
import com.aerobook.auth.dto.RegisterRequest;

import java.util.List;

/**
 * Core authentication service contract for the AeroBook Flight Reservation
 * System.
 *
 * <p>
 * Defines operations for:
 * <ul>
 * <li>User and administrator registration and credential synchronization</li>
 * <li>Password authentication and JWT token issuance</li>
 * <li>Refresh token rotation and lifecycle management</li>
 * <li>Administrative credential inspection and dynamic role updates</li>
 * </ul>
 * </p>
 *
 * @author AeroBook Development Team
 * @version 1.0
 */
public interface AuthService {

    /**
     * Registers a new customer account with default {@code ROLE_USER}
     * privileges.
     *
     * <p>
     * Flow involves:
     * <ol>
     * <li>Verifying email uniqueness within the credential repository</li>
     * <li>Calling the downstream {@code user-service} via OpenFeign to persist
     * customer profile</li>
     * <li>Hashing the plain-text password using BCrypt</li>
     * <li>Persisting the credential record</li>
     * <li>Issuing an initial JWT access token and refresh token</li>
     * </ol>
     * </p>
     *
     * @param request the registration details including name, email, phone, and
     * password
     * @return {@link AuthResponse} containing access token, refresh token,
     * token type, role, and userId
     * @throws com.aerobook.auth.exception.InvalidCredentialsException if the
     * email is already registered
     */
    AuthResponse register(RegisterRequest request);

    /**
     * Registers or promotes an account to {@code ROLE_ADMIN} using master setup
     * credentials.
     *
     * <p>
     * If a credential for the email already exists, its role is upgraded to
     * {@code ROLE_ADMIN} and synced with {@code user-service}. If new, a new
     * admin profile is created.</p>
     *
     * @param request the admin registration request containing credentials and
     * profile info
     * @return {@link AuthResponse} containing signed tokens with
     * {@code ROLE_ADMIN} claims
     */
    AuthResponse registerAdmin(RegisterRequest request);

    /**
     * Authenticates user email and password credentials against BCrypt hashed
     * storage.
     *
     * @param request the login credentials containing email and plain-text
     * password
     * @return {@link AuthResponse} containing JWT access token and refresh
     * token upon successful authentication
     * @throws com.aerobook.auth.exception.ResourceNotFoundException if no user
     * exists for the given email
     * @throws com.aerobook.auth.exception.InvalidCredentialsException if the
     * provided password does not match
     */
    AuthResponse login(LoginRequest request);

    /**
     * Exchanges an unexpired refresh token for a newly issued JWT access token.
     *
     * @param request the refresh token exchange request
     * @return {@link AuthResponse} with refreshed access token and preserved or
     * rotated refresh token
     * @throws com.aerobook.auth.exception.ResourceNotFoundException if refresh
     * token is not found or user is missing
     * @throws RuntimeException if the refresh token has expired
     */
    AuthResponse refreshToken(RefreshTokenRequest request);

    /**
     * Retrieves all registered user credentials in the system for
     * administrative auditing.
     *
     * @return {@link List} of {@link CredentialResponse} containing masked user
     * credential overviews
     */
    List<CredentialResponse> getAllCredentials();

    /**
     * Updates the authorization security role for a specific user.
     *
     * @param userId the unique identifier of the user in user-service
     * @param role the target role name (e.g., "ROLE_USER" or "ROLE_ADMIN")
     * @return {@link CredentialResponse} containing the updated credential
     * details
     * @throws com.aerobook.auth.exception.ResourceNotFoundException if no
     * credential is found for the given user ID
     * @throws IllegalArgumentException if the provided role name is not valid
     */
    CredentialResponse updateRole(Long userId, String role);
}
