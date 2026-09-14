package com.aerobook.auth.service;

import com.aerobook.auth.client.UserClient;
import com.aerobook.auth.dto.AuthResponse;
import com.aerobook.auth.dto.CredentialResponse;
import com.aerobook.auth.dto.LoginRequest;
import com.aerobook.auth.dto.RefreshTokenRequest;
import com.aerobook.auth.dto.RegisterRequest;
import com.aerobook.auth.dto.UserCreateRequest;
import com.aerobook.auth.dto.UserResponse;
import com.aerobook.auth.entity.Credential;
import com.aerobook.auth.entity.RefreshToken;
import com.aerobook.auth.entity.Role;
import com.aerobook.auth.exception.InvalidCredentialsException;
import com.aerobook.auth.exception.ResourceNotFoundException;
import com.aerobook.auth.repository.CredentialRepository;
import com.aerobook.auth.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Isolated unit test suite for {@link AuthServiceImpl} verifying registration,
 * authentication, refresh token exchange, role elevation, and security
 * constraints.
 *
 * @author AeroBook Development Team
 * @version 1.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Auth Service Implementation Unit Tests")
class AuthServiceImplTest {

    /**
     * Mocked repository for credential lookup and persistence.
     */
    @Mock
    private CredentialRepository credentialRepository;

    /**
     * Mocked password encoder for validating and hashing passwords.
     */
    @Mock
    private PasswordEncoder passwordEncoder;

    /**
     * Mocked JWT service for token generation and signature verification.
     */
    @Mock
    private JwtService jwtService;

    /**
     * Mocked refresh token service for token persistence and verification.
     */
    @Mock
    private RefreshTokenService refreshTokenService;

    /**
     * Mocked Feign client for user-service profile synchronization.
     */
    @Mock
    private UserClient userClient;

    /**
     * Injected instance of the service under test.
     */
    @InjectMocks
    private AuthServiceImpl authService;

    /**
     * Reusable test credential entity.
     */
    private Credential testCredential;

    /**
     * Reusable test refresh token entity.
     */
    private RefreshToken testRefreshToken;

    /**
     * Prepares common test fixtures and sets admin whitelist configuration
     * prior to each test case.
     */
    @BeforeEach
    void setUp() {
        // Initialize configurable admin email whitelist
        ReflectionTestUtils.setField(authService, "adminEmails", "admin@aerobook.com,admin1@aerobook.com");

        testCredential = new Credential();
        testCredential.setCredentialId(1L);
        testCredential.setUserId(5L);
        testCredential.setEmail("asha.khan@example.com");
        testCredential.setPassword("hashedPassword123");
        testCredential.setRole(Role.ROLE_USER);
        testCredential.setCreatedAt(LocalDateTime.now());
        testCredential.setUpdatedAt(LocalDateTime.now());

        testRefreshToken = new RefreshToken();
        testRefreshToken.setId(10L);
        testRefreshToken.setCredentialId(1L);
        testRefreshToken.setToken("sample-refresh-token-uuid");
        testRefreshToken.setExpiryDate(LocalDateTime.now().plusDays(7));
    }

    // ========================================================================
    // 1. LOGIN TESTS
    // ========================================================================
    @Test
    @DisplayName("Should successfully authenticate valid credentials and issue tokens")
    void login_Success_ReturnsAuthResponse() {
        LoginRequest request = new LoginRequest();
        request.setEmail("asha.khan@example.com");
        request.setPassword("Asha@12345");

        when(credentialRepository.findByEmail("asha.khan@example.com")).thenReturn(Optional.of(testCredential));
        when(passwordEncoder.matches("Asha@12345", "hashedPassword123")).thenReturn(true);
        when(jwtService.generateToken(anyString(), anyString())).thenReturn("mocked.jwt.access.token");
        when(refreshTokenService.createRefreshToken(1L)).thenReturn(testRefreshToken);

        AuthResponse response = authService.login(request);

        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("mocked.jwt.access.token");
        assertThat(response.getRefreshToken()).isEqualTo("sample-refresh-token-uuid");
        assertThat(response.getRole()).isEqualTo(Role.ROLE_USER.name());
        assertThat(response.getUserId()).isEqualTo(5L);
        assertThat(response.getTokenType()).isEqualTo("Bearer");
    }

    @Test
    @DisplayName("Should reject authentication and throw exception when password does not match")
    void login_InvalidPassword_ThrowsInvalidCredentialsException() {
        LoginRequest request = new LoginRequest();
        request.setEmail("asha.khan@example.com");
        request.setPassword("WrongPassword");

        when(credentialRepository.findByEmail("asha.khan@example.com")).thenReturn(Optional.of(testCredential));
        when(passwordEncoder.matches("WrongPassword", "hashedPassword123")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessageContaining("Invalid credentials");

        verify(jwtService, never()).generateToken(anyString(), anyString());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when user email does not exist")
    void login_UserNotFound_ThrowsResourceNotFoundException() {
        LoginRequest request = new LoginRequest();
        request.setEmail("missing@example.com");
        request.setPassword("password");

        when(credentialRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User not found");
    }

    // ========================================================================
    // 2. REGISTRATION TESTS
    // ========================================================================
    @Test
    @DisplayName("Should successfully register a new user and return AuthResponse")
    void register_NewUser_Success() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("newuser@example.com");
        request.setPassword("Password@123");
        request.setFirstName("New");
        request.setLastName("User");

        when(credentialRepository.findByEmail("newuser@example.com")).thenReturn(Optional.empty());
        when(userClient.createUser(any(UserCreateRequest.class))).thenReturn(new UserResponse(10L, "newuser@example.com"));
        when(passwordEncoder.encode("Password@123")).thenReturn("encodedPassword");
        when(credentialRepository.save(any(Credential.class))).thenAnswer(invocation -> {
            Credential c = invocation.getArgument(0);
            c.setCredentialId(10L);
            return c;
        });
        when(jwtService.generateToken(anyString(), anyString())).thenReturn("mocked.jwt.access.token");
        when(refreshTokenService.createRefreshToken(10L)).thenReturn(testRefreshToken);

        AuthResponse response = authService.register(request);

        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("mocked.jwt.access.token");
        assertThat(response.getRole()).isEqualTo(Role.ROLE_USER.name());
        assertThat(response.getUserId()).isEqualTo(10L);
    }

    @Test
    @DisplayName("Should throw InvalidCredentialsException when registering with an existing email")
    void register_ExistingUser_ThrowsInvalidCredentialsException() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("asha.khan@example.com");
        request.setPassword("Password@123");

        when(credentialRepository.findByEmail("asha.khan@example.com")).thenReturn(Optional.of(testCredential));

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessageContaining("Email already exists");
    }

    @Test
    @DisplayName("Should automatically promote to ROLE_ADMIN when registered email matches admin whitelist")
    void register_AdminEmail_AutoPromotesToAdmin() {
        testCredential.setEmail("admin@aerobook.com");
        RegisterRequest request = new RegisterRequest();
        request.setEmail("admin@aerobook.com");
        request.setPassword("AdminSecret@123");

        when(credentialRepository.findByEmail("admin@aerobook.com")).thenReturn(Optional.of(testCredential));
        when(userClient.updateUser(eq(5L), any(UserCreateRequest.class))).thenReturn(new UserResponse(5L, "admin@aerobook.com"));
        when(credentialRepository.save(any(Credential.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(jwtService.generateToken(anyString(), anyString())).thenReturn("admin.jwt.token");
        when(refreshTokenService.createRefreshToken(1L)).thenReturn(testRefreshToken);

        AuthResponse response = authService.register(request);

        assertThat(response).isNotNull();
        assertThat(response.getRole()).isEqualTo(Role.ROLE_ADMIN.name());
        assertThat(response.getAccessToken()).isEqualTo("admin.jwt.token");
    }

    @Test
    @DisplayName("Should register admin account directly via registerAdmin")
    void registerAdmin_NewAdmin_Success() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("superadmin@aerobook.com");
        request.setPassword("SuperAdmin@123");

        when(credentialRepository.findByEmail("superadmin@aerobook.com")).thenReturn(Optional.empty());
        when(userClient.createUser(any(UserCreateRequest.class))).thenReturn(new UserResponse(20L, "superadmin@aerobook.com"));
        when(passwordEncoder.encode("SuperAdmin@123")).thenReturn("encodedAdminPassword");
        when(credentialRepository.save(any(Credential.class))).thenAnswer(invocation -> {
            Credential c = invocation.getArgument(0);
            c.setCredentialId(20L);
            return c;
        });
        when(jwtService.generateToken(anyString(), anyString())).thenReturn("admin.token");
        when(refreshTokenService.createRefreshToken(20L)).thenReturn(testRefreshToken);

        AuthResponse response = authService.registerAdmin(request);

        assertThat(response).isNotNull();
        assertThat(response.getRole()).isEqualTo(Role.ROLE_ADMIN.name());
        assertThat(response.getUserId()).isEqualTo(20L);
    }

    // ========================================================================
    // 3. REFRESH TOKEN TESTS
    // ========================================================================
    @Test
    @DisplayName("Should successfully refresh token when given a valid refresh token")
    void refreshToken_Success_ReturnsNewAuthResponse() {
        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken("sample-refresh-token-uuid");

        when(refreshTokenService.verifyRefreshToken("sample-refresh-token-uuid")).thenReturn(testRefreshToken);
        when(credentialRepository.findById(1L)).thenReturn(Optional.of(testCredential));
        when(jwtService.generateToken(anyString(), anyString())).thenReturn("new.mocked.jwt.access.token");

        AuthResponse response = authService.refreshToken(request);

        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("new.mocked.jwt.access.token");
        assertThat(response.getRefreshToken()).isEqualTo("sample-refresh-token-uuid");
        assertThat(response.getUserId()).isEqualTo(5L);
        assertThat(response.getRole()).isEqualTo(Role.ROLE_USER.name());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when refresh token does not match any credential")
    void refreshToken_CredentialNotFound_ThrowsResourceNotFoundException() {
        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken("sample-refresh-token-uuid");

        when(refreshTokenService.verifyRefreshToken("sample-refresh-token-uuid")).thenReturn(testRefreshToken);
        when(credentialRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.refreshToken(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Credential not found");
    }

    // ========================================================================
    // 4. ROLE MANAGEMENT & AUDITING TESTS
    // ========================================================================
    @Test
    @DisplayName("Should successfully promote user role to ROLE_ADMIN")
    void updateRole_Success_PromotesUserToAdmin() {
        when(credentialRepository.findByUserId(5L)).thenReturn(Optional.of(testCredential));
        when(credentialRepository.save(any(Credential.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CredentialResponse response = authService.updateRole(5L, "ROLE_ADMIN");

        assertThat(response).isNotNull();
        assertThat(response.getUserId()).isEqualTo(5L);
        assertThat(response.getRole()).isEqualTo("ROLE_ADMIN");
        verify(credentialRepository).save(testCredential);
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when attempting to set an invalid role")
    void updateRole_InvalidRole_ThrowsIllegalArgumentException() {
        when(credentialRepository.findByUserId(5L)).thenReturn(Optional.of(testCredential));

        assertThatThrownBy(() -> authService.updateRole(5L, "ROLE_SUPERUSER"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid role: ROLE_SUPERUSER");

        verify(credentialRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should retrieve all credentials mapped to CredentialResponse objects")
    void getAllCredentials_ReturnsListOfCredentials() {
        when(credentialRepository.findAll()).thenReturn(List.of(testCredential));

        List<CredentialResponse> credentials = authService.getAllCredentials();

        assertThat(credentials).hasSize(1);
        assertThat(credentials.get(0).getEmail()).isEqualTo("asha.khan@example.com");
        assertThat(credentials.get(0).getRole()).isEqualTo("ROLE_USER");
    }
}
