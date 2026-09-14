package com.aerobook.auth.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.aerobook.auth.dto.AuthResponse;
import com.aerobook.auth.dto.LoginRequest;
import com.aerobook.auth.dto.RefreshTokenRequest;
import com.aerobook.auth.dto.RegisterRequest;
import com.aerobook.auth.dto.UserCreateRequest;
import com.aerobook.auth.client.UserClient;
import com.aerobook.auth.entity.Credential;
import com.aerobook.auth.entity.RefreshToken;
import com.aerobook.auth.entity.Role;
import com.aerobook.auth.exception.InvalidCredentialsException;
import com.aerobook.auth.exception.ResourceNotFoundException;
import com.aerobook.auth.repository.CredentialRepository;
import com.aerobook.auth.service.AuthService;
import com.aerobook.auth.service.JwtService;
import com.aerobook.auth.service.RefreshTokenService;

/**
 * Default implementation of the {@link AuthService} interface.
 *
 * <p>
 * Coordinates authentication, credential storage, profile provisioning, and
 * security token issuance:
 * <ul>
 * <li>Integrates with {@link CredentialRepository} for persistent credential
 * management</li>
 * <li>Synchronizes profile creation and updates with {@link UserClient}
 * (user-service OpenFeign client)</li>
 * <li>Hashes credentials with Spring Security's BCrypt
 * {@link PasswordEncoder}</li>
 * <li>Issues signed bearer JWTs via {@link JwtService} and persists refresh
 * tokens via {@link RefreshTokenService}</li>
 * <li>Supports administrative role upgrades, user auditing, and whitelist-based
 * admin auto-promotion</li>
 * </ul>
 * </p>
 *
 * @author AeroBook Development Team
 * @version 1.0
 */
@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

    /**
     * Repository for accessing and managing persisted user credential records.
     */
    private final CredentialRepository credentialRepository;

    /**
     * Cryptographic encoder for BCrypt password hashing and verification.
     */
    private final PasswordEncoder passwordEncoder;

    /**
     * Service for generating signed JWT access tokens and extracting claims.
     */
    private final JwtService jwtService;

    /**
     * Service for creating, validating, and revoking database-backed refresh
     * tokens.
     */
    private final RefreshTokenService refreshTokenService;

    /**
     * Feign client for synchronizing profile data with the downstream
     * user-service.
     */
    private final UserClient userClient;

    /**
     * Comma-separated list of administrative email addresses auto-granted
     * ROLE_ADMIN.
     */
    @Value("${auth.admin.emails:admin@aerobook.com}")
    private String adminEmails;

    /**
     * Constructs the authentication service implementation with required
     * dependencies.
     *
     * @param credentialRepository the repository for managing user credentials
     * @param passwordEncoder the BCrypt password hashing component
     * @param jwtService the JSON Web Token issuance service
     * @param refreshTokenService the refresh token lifecycle management service
     * @param userClient the Feign client targeting user-service
     */
    public AuthServiceImpl(
            CredentialRepository credentialRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            RefreshTokenService refreshTokenService,
            UserClient userClient) {

        this.credentialRepository = credentialRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.userClient = userClient;
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * Registers a user account. If the email is configured in
     * {@code auth.admin.emails}, the user is automatically provisioned as an
     * administrator with {@code ROLE_ADMIN}. Otherwise, a standard customer
     * account with {@code ROLE_USER} is registered.</p>
     */
    @Override
    public AuthResponse register(RegisterRequest request) {

        log.info("Processing user registration for email: {}", request.getEmail());

        // Step 1: Check if the registering email qualifies for administrative privileges
        boolean requestedAdmin = isAdminEmail(request.getEmail());
        java.util.Optional<Credential> existingCredential
                = credentialRepository.findByEmail(request.getEmail());

        // Step 2: Prevent duplicate registrations for regular customer accounts
        if (existingCredential.isPresent() && !requestedAdmin) {
            log.warn("Registration rejected: Account with email {} already exists", request.getEmail());
            throw new InvalidCredentialsException("Email already exists");
        }

        // Step 3: Upgrade existing credential to admin if email matches the admin whitelist
        if (existingCredential.isPresent()) {
            Credential credential = existingCredential.get();
            credential.setRole(Role.ROLE_ADMIN);
            // Synchronize updated role with user-service
            userClient.updateUser(credential.getUserId(),
                    new UserCreateRequest(request, Role.ROLE_ADMIN.name()));
            credential = credentialRepository.save(credential);
            log.info("Existing user {} promoted to ROLE_ADMIN via whitelist", credential.getEmail());
            return createAuthResponse(credential);
        }

        // Step 4: Determine role and call user-service downstream to persist profile
        Role role = requestedAdmin ? Role.ROLE_ADMIN : Role.ROLE_USER;
        com.aerobook.auth.dto.UserResponse user = userClient.createUser(
                new UserCreateRequest(request, role.name()));
        Long userId = user.getUserId();

        // Step 5: Save hashed credentials locally in aerobook_auth_db
        Credential credential = new Credential();
        credential.setUserId(userId);
        credential.setEmail(request.getEmail());
        credential.setPassword(
                passwordEncoder.encode(request.getPassword()));
        credential.setRole(role);

        credential = credentialRepository.save(credential);
        log.info("User registered successfully with email: {}, user ID: {}, role: {}", credential.getEmail(), credential.getUserId(), credential.getRole());

        // Step 6: Issue access token and refresh token
        return createAuthResponse(credential);
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * Registers a guaranteed administrator account with {@code ROLE_ADMIN},
     * bypassing customer whitelist checks.</p>
     */
    @Override
    public AuthResponse registerAdmin(RegisterRequest request) {
        log.info("Processing administrator registration for email: {}", request.getEmail());
        java.util.Optional<Credential> existingCredential
                = credentialRepository.findByEmail(request.getEmail());

        // If user already exists, update their profile and upgrade their role to ROLE_ADMIN
        if (existingCredential.isPresent()) {
            Credential credential = existingCredential.get();
            userClient.updateUser(credential.getUserId(),
                    new UserCreateRequest(request, Role.ROLE_ADMIN.name()));
            credential.setPassword(
                    passwordEncoder.encode(request.getPassword()));
            credential.setRole(Role.ROLE_ADMIN);
            Credential saved = credentialRepository.save(credential);
            log.info("Existing user {} elevated to ROLE_ADMIN", saved.getEmail());
            return createAuthResponse(saved);
        }

        // Otherwise, provision a brand-new administrator in user-service and auth-service
        com.aerobook.auth.dto.UserResponse user = userClient.createUser(
                new UserCreateRequest(request, Role.ROLE_ADMIN.name()));
        Credential credential = new Credential();
        credential.setUserId(user.getUserId());
        credential.setEmail(request.getEmail());
        credential.setPassword(
                passwordEncoder.encode(request.getPassword()));
        credential.setRole(Role.ROLE_ADMIN);

        Credential saved = credentialRepository.save(credential);
        log.info("Admin account successfully provisioned for email: {}, user ID: {}", saved.getEmail(), saved.getUserId());

        return createAuthResponse(saved);
    }

    /**
     * Helper method to generate both JWT access token and database-backed
     * refresh token.
     *
     * @param credential the authenticated user credential entity
     * @return populated {@link AuthResponse} containing tokens and user
     * metadata
     */
    private AuthResponse createAuthResponse(Credential credential) {
        // Generate signed JWT access token containing subject email and authority role
        String accessToken
                = jwtService.generateToken(
                        credential.getEmail(), credential.getRole().name());

        // Generate persistent refresh token valid for 7 days
        RefreshToken refreshToken
                = refreshTokenService.createRefreshToken(
                        credential.getCredentialId());

        return new AuthResponse(
                accessToken,
                refreshToken.getToken(),
                "Bearer",
                credential.getRole().name(),
                credential.getUserId());
    }

    /**
     * Helper method to verify whether an email address is listed in the admin
     * emails whitelist.
     *
     * @param email the candidate email address to evaluate
     * @return {@code true} if matching configured admin emails; {@code false}
     * otherwise
     */
    private boolean isAdminEmail(String email) {
        return java.util.Arrays.stream(adminEmails.split(","))
                .map(String::trim)
                .anyMatch(configuredEmail
                        -> configuredEmail.equalsIgnoreCase(email));
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * Locates credentials by email and validates the plain-text password using
     * BCrypt. On success, issues a new JWT and refresh token.</p>
     */
    @Override
    public AuthResponse login(LoginRequest request) {

        log.info("Processing login request for email: {}", request.getEmail());

        // Step 1: Look up user credential by email
        Credential credential
                = credentialRepository
                        .findByEmail(request.getEmail())
                        .orElseThrow(()
                                -> {
                            log.warn("Login failed: User not found for email: {}", request.getEmail());
                            return new ResourceNotFoundException("User not found");
                        });

        // Step 2: Validate provided password against BCrypt hashed secret
        if (!passwordEncoder.matches(
                request.getPassword(),
                credential.getPassword())) {

            log.warn("Login failed: Invalid password for email: {}", request.getEmail());
            throw new InvalidCredentialsException(
                    "Invalid credentials");
        }

        // Step 3: Issue authentication tokens
        String accessToken
                = jwtService.generateToken(
                        credential.getEmail(), credential.getRole().name());

        RefreshToken refreshToken
                = refreshTokenService.createRefreshToken(
                        credential.getCredentialId());

        log.info("User {} successfully authenticated with role: {}", credential.getEmail(), credential.getRole());

        return new AuthResponse(
                accessToken,
                refreshToken.getToken(),
                "Bearer",
                credential.getRole().name(),
                credential.getUserId());
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * Validates the unexpired refresh token, retrieves the associated
     * credential, and signs a fresh JWT access token.</p>
     */
    @Override
    public AuthResponse refreshToken(
            RefreshTokenRequest request) {

        log.info("Processing refresh token request");

        // Step 1: Verify token existence and expiration
        RefreshToken refreshToken
                = refreshTokenService.verifyRefreshToken(
                        request.getRefreshToken());

        // Step 2: Retrieve the corresponding user credential
        Credential credential
                = credentialRepository
                        .findById(
                                refreshToken.getCredentialId())
                        .orElseThrow(()
                                -> new ResourceNotFoundException(
                                "Credential not found"));

        // Step 3: Generate fresh access token
        String accessToken
                = jwtService.generateToken(
                        credential.getEmail(), credential.getRole().name());

        log.info("Issued refreshed access token for user ID: {} ({})", credential.getUserId(), credential.getEmail());

        return new AuthResponse(
                accessToken,
                refreshToken.getToken(),
                "Bearer",
                credential.getRole().name(),
                credential.getUserId());
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * Retrieves all credentials from the database and maps them to secure
     * response DTOs.</p>
     */
    @Override
    public java.util.List<com.aerobook.auth.dto.CredentialResponse> getAllCredentials() {
        return credentialRepository.findAll().stream()
                .map(c -> new com.aerobook.auth.dto.CredentialResponse(
                c.getCredentialId(),
                c.getUserId(),
                c.getEmail(),
                c.getRole().name(),
                c.getCreatedAt(),
                c.getUpdatedAt()
        ))
                .toList();
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * Validates the new role string against {@link Role} enum values, applies
     * the update, and persists the modified credential.</p>
     */
    @Override
    public com.aerobook.auth.dto.CredentialResponse updateRole(Long userId, String roleName) {
        log.info("Updating authority role for user ID: {} to {}", userId, roleName);

        // Step 1: Verify credential exists for the given user ID
        Credential credential = credentialRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Credential not found for user ID: " + userId));

        // Step 2: Validate and parse role enum
        Role newRole;
        try {
            newRole = Role.valueOf(roleName.toUpperCase().trim());
        } catch (Exception ex) {
            log.warn("Invalid role upgrade attempt '{}' for user ID: {}", roleName, userId);
            throw new IllegalArgumentException("Invalid role: " + roleName + ". Allowed roles: ROLE_USER, ROLE_ADMIN");
        }

        // Step 3: Update role and save changes
        credential.setRole(newRole);
        Credential saved = credentialRepository.save(credential);
        log.info("Successfully updated user ID {} role to {}", userId, newRole);

        // Step 4: Map to response DTO
        return new com.aerobook.auth.dto.CredentialResponse(
                saved.getCredentialId(),
                saved.getUserId(),
                saved.getEmail(),
                saved.getRole().name(),
                saved.getCreatedAt(),
                saved.getUpdatedAt()
        );
    }
}
