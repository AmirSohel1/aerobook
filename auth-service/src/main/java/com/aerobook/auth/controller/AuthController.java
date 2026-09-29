package com.aerobook.auth.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aerobook.auth.dto.AuthResponse;
import com.aerobook.auth.dto.CredentialResponse;
import com.aerobook.auth.dto.ErrorResponse;
import com.aerobook.auth.dto.LoginRequest;
import com.aerobook.auth.dto.RefreshTokenRequest;
import com.aerobook.auth.dto.RegisterRequest;
import com.aerobook.auth.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * ============================================================================
 * Central Authentication & Security Controller (Port 8082)
 * ============================================================================
 *
 * REST Controller exposing user registration, administrator setup, JWT
 * authentication, token exchange, and administrative role management.
 *
 * <p>
 * Key Endpoints:
 * <ul>
 * <li>{@code GET  /api/auth/health}: Operational health check</li>
 * <li>{@code POST /api/auth/register}: Customer account registration
 * (ROLE_USER)</li>
 * <li>{@code POST /api/auth/register-admin}: Protected administrator setup
 * (ROLE_ADMIN)</li>
 * <li>{@code POST /api/auth/login}: Credential verification & JWT issuance</li>
 * <li>{@code POST /api/auth/refresh-token}: Token exchange for new access
 * token</li>
 * <li>{@code GET  /api/auth/credentials}: Administrative credential audit</li>
 * <li>{@code PUT  /api/auth/credentials/{userId}/role}: Administrative role
 * promotion/demotion</li>
 * </ul>
 *
 * @author Aerobook Security Engineering
 * @version 1.0.0
 */
@Tag(name = "Authentication", description = "Endpoints for user registration, admin setup, authentication, and JWT token refresh")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    /**
     * Master setup key required to register or elevate accounts to ROLE_ADMIN.
     */
    @Value("${auth.admin.registration.key:AerobookAdminSetup2026}")
    private String adminRegistrationKey;

    /**
     * Master setup key required to register or elevate accounts to ROLE_STAFF.
     */
    @Value("${auth.staff.registration.key:AerobookStaffSetup2026}")
    private String staffRegistrationKey;

    /**
     * Constructs AuthController with required authentication service
     * dependency.
     *
     * @param authService business service handling credentials and tokens
     */
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Service health and liveness probe.
     *
     * @return {@link ResponseEntity} containing service status and timestamp
     */
    @Operation(summary = "Auth Service Health Probe", description = "Public health check to verify Auth Service status.", security = {})
    @ApiResponse(responseCode = "200", description = "Auth Service is operational")
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        return ResponseEntity.ok(Map.of(
                "service", "auth-service",
                "status", "UP",
                "port", 8082,
                "timestamp", LocalDateTime.now().toString()
        ));
    }

    /**
     * Registers a new customer account with standard ROLE_USER authority.
     * Hashes password, syncs profile with user-service, and returns JWT access
     * and refresh tokens.
     *
     * @param request customer registration details (name, email, phone, birth
     * date, password)
     * @return {@link ResponseEntity} with HTTP 201 Created and issued
     * {@link AuthResponse}
     */
    @Operation(
            summary = "Register customer account",
            description = "Creates a new customer user account with ROLE_USER, registers profile with user-service, and returns JWT access and refresh tokens.",
            security = {}
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Customer account registered successfully",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthResponse.class))),
        @ApiResponse(responseCode = "400", description = "Validation error or invalid request payload",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "409", description = "Conflict: User with provided email already exists",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Customer user registration details",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = RegisterRequest.class),
                            examples = @ExampleObject(
                                    name = "Sample Customer Registration",
                                    summary = "Register customer Asha Khan",
                                    value = """
                                            {
                                              "firstName": "Asha",
                                              "lastName": "Khan",
                                              "email": "asha.khan@example.com",
                                              "phoneNumber": "9876543210",
                                              "dateOfBirth": "1998-05-12",
                                              "nationality": "Indian",
                                              "password": "Asha@12345"
                                            }
                                            """
                            )
                    )
            )
            @Valid @RequestBody RegisterRequest request
    ) {
        AuthResponse response = authService.register(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Registers or promotes an account to administrative authority
     * (ROLE_ADMIN). Strictly requires the secret X-Admin-Setup-Key header.
     *
     * @param setupKey secret header matching auth.admin.registration.key
     * @param request administrator account details
     * @return {@link ResponseEntity} with HTTP 201 Created and admin
     * {@link AuthResponse}
     */
    @Operation(
            summary = "Register or promote administrator account",
            description = "Registers a new administrator or promotes an existing account to ROLE_ADMIN. Requires the secret X-Admin-Setup-Key header.",
            security = {}
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Administrator registered successfully",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthResponse.class))),
        @ApiResponse(responseCode = "403", description = "Forbidden: Invalid or missing X-Admin-Setup-Key",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "400", description = "Validation error",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/register-admin")
    public ResponseEntity<?> registerAdmin(
            @Parameter(
                    name = "X-Admin-Setup-Key",
                    description = "Secret key required for administrator elevation",
                    required = true,
                    in = ParameterIn.HEADER,
                    example = "AerobookAdminSetup2026"
            )
            @RequestHeader(value = "X-Admin-Setup-Key", required = false) String setupKey,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Administrator account payload",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = RegisterRequest.class),
                            examples = @ExampleObject(
                                    name = "Sample Admin Registration",
                                    summary = "Register master administrator",
                                    value = """
                                            {
                                              "firstName": "Aerobook",
                                              "lastName": "Admin",
                                              "email": "admin@aerobook.com",
                                              "phoneNumber": "9876543200",
                                              "dateOfBirth": "1990-01-01",
                                              "nationality": "Indian",
                                              "password": "Admin@12345"
                                            }
                                            """
                            )
                    )
            )
            @Valid @RequestBody RegisterRequest request
    ) {
        if (setupKey == null || !setupKey.equals(adminRegistrationKey)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResponse(
                            LocalDateTime.now(),
                            HttpStatus.FORBIDDEN.value(),
                            "Forbidden",
                            "Invalid or missing admin registration setup key. Access denied."
                    ));
        }

        AuthResponse response = authService.registerAdmin(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Registers or promotes an account to staff authority (ROLE_STAFF).
     * Strictly requires the secret X-Staff-Setup-Key header.
     *
     * @param setupKey secret header matching auth.staff.registration.key
     * @param request staff account details
     * @return {@link ResponseEntity} with HTTP 201 Created and staff
     * {@link AuthResponse}
     */
    @Operation(
            summary = "Register or promote staff account",
            description = "Registers a new staff member or promotes an existing account to ROLE_STAFF for ground and flight operations. Requires the secret X-Staff-Setup-Key header.",
            security = {}
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Staff member registered successfully",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthResponse.class))),
        @ApiResponse(responseCode = "403", description = "Forbidden: Invalid or missing X-Staff-Setup-Key",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "400", description = "Validation error",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/register-staff")
    public ResponseEntity<?> registerStaff(
            @Parameter(
                    name = "X-Staff-Setup-Key",
                    description = "Secret key required for staff elevation",
                    required = true,
                    in = ParameterIn.HEADER,
                    example = "AerobookStaffSetup2026"
            )
            @RequestHeader(value = "X-Staff-Setup-Key", required = false) String setupKey,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Staff account payload",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = RegisterRequest.class),
                            examples = @ExampleObject(
                                    name = "Sample Staff Registration",
                                    summary = "Register airport ground staff",
                                    value = """
                                            {
                                              "firstName": "Ground",
                                              "lastName": "Officer",
                                              "email": "staff@aerobook.com",
                                              "phoneNumber": "9876543201",
                                              "dateOfBirth": "1994-04-15",
                                              "nationality": "Indian",
                                              "password": "Staff@12345"
                                            }
                                            """
                            )
                    )
            )
            @Valid @RequestBody RegisterRequest request
    ) {
        if (setupKey == null || !setupKey.equals(staffRegistrationKey)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResponse(
                            LocalDateTime.now(),
                            HttpStatus.FORBIDDEN.value(),
                            "Forbidden",
                            "Invalid or missing staff registration setup key. Access denied."
                    ));
        }

        AuthResponse response = authService.registerStaff(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Authenticates user email and password credentials, returning signed JWT
     * access and refresh tokens.
     *
     * @param request email and password credentials
     * @return {@link ResponseEntity} with HTTP 200 OK and issued
     * {@link AuthResponse}
     */
    @Operation(
            summary = "Authenticate user and get JWT tokens",
            description = "Validates registered email and password credentials. Returns signed JWT bearer access token (1 hr) and refresh token (7 days).",
            security = {}
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Authentication successful, tokens issued",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized: Invalid email or password credentials",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request: Missing or invalid request fields",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Login credentials",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = LoginRequest.class),
                            examples = @ExampleObject(
                                    name = "Customer Login Example",
                                    summary = "Log in with registered customer credentials",
                                    value = """
                                            {
                                              "email": "asha.khan@example.com",
                                              "password": "Asha@12345"
                                            }
                                            """
                            )
                    )
            )
            @Valid @RequestBody LoginRequest request
    ) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Exchanges a valid unexpired refresh token for a newly signed JWT access
     * token.
     *
     * @param request refresh token payload
     * @return {@link ResponseEntity} with HTTP 200 OK and refreshed
     * {@link AuthResponse}
     */
    @Operation(
            summary = "Refresh expired access token",
            description = "Generates a new JWT access token using a valid unexpired refresh token without requiring re-entry of password.",
            security = {}
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Access token refreshed successfully",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized: Refresh token expired or revoked",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Not Found: Refresh token does not exist in registry",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponse> refreshToken(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Refresh token exchange request",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = RefreshTokenRequest.class),
                            examples = @ExampleObject(
                                    name = "Refresh Token Example",
                                    summary = "Exchange refresh token for new access token",
                                    value = """
                                            {
                                              "refreshToken": "d9a3b64c-8f4b-4890-a241-119c67bc2f20"
                                            }
                                            """
                            )
                    )
            )
            @Valid @RequestBody RefreshTokenRequest request
    ) {
        AuthResponse response = authService.refreshToken(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Lists all registered user credentials and roles. Restricted to
     * administrators (ROLE_ADMIN).
     *
     * @return {@link ResponseEntity} containing list of
     * {@link CredentialResponse} records
     */
    @Operation(
            summary = "List all platform credentials (ROLE_ADMIN)",
            description = "Administrative oversight endpoint to view all registered credentials and assigned roles. Requires ROLE_ADMIN authority.",
            security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Platform credentials retrieved successfully",
                content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = CredentialResponse.class)))),
        @ApiResponse(responseCode = "401", description = "Unauthorized: Missing or invalid JWT access token",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "403", description = "Forbidden: Caller does not possess ROLE_ADMIN authority",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/credentials")
    public ResponseEntity<List<CredentialResponse>> getAllCredentials() {
        return ResponseEntity.ok(authService.getAllCredentials());
    }

    /**
     * Promotes or demotes an account between ROLE_USER and ROLE_ADMIN.
     * Restricted to administrators.
     *
     * @param userId user account database identifier
     * @param role target authority role name (ROLE_USER or ROLE_ADMIN)
     * @return {@link ResponseEntity} with updated {@link CredentialResponse}
     */
    @Operation(
            summary = "Update user security role (ROLE_ADMIN)",
            description = "Promotes or demotes an account between ROLE_USER and ROLE_ADMIN. Restricted to ROLE_ADMIN.",
            security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Role updated successfully",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = CredentialResponse.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request: Invalid role name",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Not Found: User ID not found",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "403", description = "Forbidden: Caller lacks ROLE_ADMIN authority",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/credentials/{userId}/role")
    public ResponseEntity<CredentialResponse> updateUserRole(
            @Parameter(name = "userId", description = "User ID to update", required = true, example = "1")
            @PathVariable Long userId,
            @Parameter(name = "role", description = "Target security role (ROLE_USER or ROLE_ADMIN)", required = true, example = "ROLE_ADMIN")
            @RequestParam String role) {
        return ResponseEntity.ok(authService.updateRole(userId, role));
    }
}
