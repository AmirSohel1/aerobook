package com.aerobook.user.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aerobook.user.dto.request.UserRequest;
import com.aerobook.user.dto.response.ErrorResponse;
import com.aerobook.user.dto.response.UserResponse;
import com.aerobook.user.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
 * User Profile Management REST Controller
 * ============================================================================
 *
 * Exposes customer-facing profile query/mutation APIs and administrative user
 * directory controls under base path {@code /api/v1/users}.
 *
 * <p>
 * Access Policy Summary:
 * <ul>
 * <li>{@code GET /health} - Public operational probe</li>
 * <li>{@code GET /{id}}, {@code GET /email/{email}}, {@code PUT /{id}} -
 * Customer owner or {@code ROLE_ADMIN}</li>
 * <li>{@code GET /}, {@code POST /}, {@code DELETE /{id}} - Strictly
 * {@code ROLE_ADMIN} only</li>
 * </ul>
 *
 * @author Aerobook Platform Engineering
 * @version 1.0.0
 */
@Tag(name = "User Management", description = "Operations for user profile management, role verification, and administration")
@SecurityRequirement(name = "BearerAuth")
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    /**
     * Constructor injection for user profile business service.
     *
     * @param userService user management service
     */
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Health check endpoint to verify User Service operational status.
     *
     * @return {@link ResponseEntity} with health confirmation message
     */
    @Operation(summary = "Service health check", description = "Checks operational health of User Service. Public endpoint.", security = {})
    @ApiResponse(responseCode = "200", description = "User Service is healthy and operational")
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("User Service is up and running!");
    }

    /**
     * Creates a new user profile record directly in User Service. Direct
     * creation via API Gateway is restricted to {@code ROLE_ADMIN}.
     *
     * @param request the validated customer profile registration payload
     * @return {@link ResponseEntity} containing created {@link UserResponse}
     * with HTTP 201 Created
     */
    @Operation(
            summary = "Create user profile (Admin Only)",
            description = "Creates a new user profile record. In production, registration occurs via Auth Service. "
            + "Direct creation via API Gateway is restricted to ROLE_ADMIN."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "User profile created successfully",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class))),
        @ApiResponse(responseCode = "400", description = "Validation error in request payload",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized: Missing or invalid JWT token"),
        @ApiResponse(responseCode = "403", description = "Forbidden: Insufficient privileges (Requires ROLE_ADMIN)"),
        @ApiResponse(responseCode = "409", description = "Conflict: User with email already exists",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<UserResponse> createUser(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "User profile registration payload",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserRequest.class),
                            examples = @ExampleObject(
                                    name = "Sample Customer User",
                                    summary = "Standard Customer Profile",
                                    value = """
                                {
                                  "firstName": "Ravi",
                                  "lastName": "Patel",
                                  "email": "ravi.patel@example.com",
                                  "phoneNumber": "9876543212",
                                  "dateOfBirth": "1995-09-20",
                                  "nationality": "Indian",
                                  "role": "ROLE_USER"
                                }
                                """
                            )
                    )
            )
            @Valid @RequestBody UserRequest request) {

        UserResponse created = userService.createUser(request);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    /**
     * Retrieves all registered user accounts. Restricted strictly to
     * administrators.
     *
     * @param userRoleHeader downstream role header propagated by API Gateway
     * @return list of all registered {@link UserResponse} profiles or HTTP 403
     * Forbidden
     */
    @Operation(
            summary = "List all registered users (Admin Only)",
            description = "Returns full list of all user profiles stored in the database. "
            + "Strictly restricted to ROLE_ADMIN to protect customer privacy."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Users retrieved successfully",
                content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = UserResponse.class)))),
        @ApiResponse(responseCode = "401", description = "Unauthorized: Missing or invalid JWT"),
        @ApiResponse(responseCode = "403", description = "Forbidden: Requires ROLE_ADMIN authority")
    })
    @GetMapping
    public ResponseEntity<?> getAllUsers(
            @Parameter(hidden = true)
            @RequestHeader(value = "X-User-Role", required = false) String userRoleHeader) {

        // Enforce administrative role verification
        if (userRoleHeader != null && !userRoleHeader.isBlank() && !"ROLE_ADMIN".equals(userRoleHeader)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResponse(java.time.LocalDateTime.now(), HttpStatus.FORBIDDEN.value(),
                            "Forbidden", "Access denied: Listing all users requires ROLE_ADMIN authority."));
        }

        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * Retrieves user profile details by unique database identifier.
     *
     * @param id user primary key
     * @return {@link ResponseEntity} with matched {@link UserResponse}
     */
    @Operation(
            summary = "Get user by ID",
            description = "Fetches detailed account information for a specific user ID. Accessible by ROLE_USER and ROLE_ADMIN."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "User profile found",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized: Missing or invalid JWT"),
        @ApiResponse(responseCode = "404", description = "User not found with given ID",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(
            @Parameter(name = "id", description = "User database identifier", example = "1", required = true)
            @PathVariable Long id) {

        UserResponse user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    /**
     * Retrieves user profile details by registered email address.
     *
     * @param email unique customer email
     * @return {@link ResponseEntity} with matched {@link UserResponse}
     */
    @Operation(
            summary = "Get user by email",
            description = "Lookup user account details by registered email address. Accessible by ROLE_USER and ROLE_ADMIN."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "User profile found",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized: Missing or invalid JWT"),
        @ApiResponse(responseCode = "404", description = "User not found with given email",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponse> getUserByEmail(
            @Parameter(name = "email", description = "User registered email address", example = "ravi.patel@example.com", required = true)
            @PathVariable String email) {

        UserResponse user = userService.getUserByEmail(email);
        return ResponseEntity.ok(user);
    }

    /**
     * Modifies an existing customer user profile coordinates.
     *
     * @param id user database identifier
     * @param request validated updated attributes payload
     * @return {@link ResponseEntity} with updated {@link UserResponse}
     */
    @Operation(
            summary = "Update user profile",
            description = "Updates personal details of an existing user account. Accessible by account owner or ROLE_ADMIN."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "User profile updated successfully",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class))),
        @ApiResponse(responseCode = "400", description = "Validation error in request payload",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized: Missing or invalid JWT"),
        @ApiResponse(responseCode = "404", description = "User not found with given ID",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @Parameter(name = "id", description = "User database ID to update", example = "1", required = true)
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Updated user profile payload",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserRequest.class),
                            examples = @ExampleObject(
                                    name = "Updated Profile Sample",
                                    summary = "Modified Customer Profile",
                                    value = """
                                {
                                  "firstName": "Ravi",
                                  "lastName": "Sharma",
                                  "email": "ravi.sharma@example.com",
                                  "phoneNumber": "9876543212",
                                  "dateOfBirth": "1995-09-20",
                                  "nationality": "Indian",
                                  "role": "ROLE_USER"
                                }
                                """
                            )
                    )
            )
            @Valid @RequestBody UserRequest request) {

        UserResponse updated = userService.updateUser(id, request);
        return ResponseEntity.ok(updated);
    }

    /**
     * Permanently deletes a customer user account from the system.
     *
     * @param id user database identifier to remove
     * @param userRoleHeader downstream role header propagated by API Gateway
     * @return confirmation message or HTTP 403 Forbidden
     */
    @Operation(
            summary = "Delete user account (Admin Only)",
            description = "Permanently deletes a user account by database ID. Strictly restricted to ROLE_ADMIN."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "User deleted successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized: Missing or invalid JWT"),
        @ApiResponse(responseCode = "403", description = "Forbidden: Requires ROLE_ADMIN authority"),
        @ApiResponse(responseCode = "404", description = "User not found with given ID",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(
            @Parameter(name = "id", description = "User database ID to delete", example = "1", required = true)
            @PathVariable Long id,
            @Parameter(hidden = true)
            @RequestHeader(value = "X-User-Role", required = false) String userRoleHeader) {

        // Validate administrative role before allowing account deletion
        if (userRoleHeader != null && !userRoleHeader.isBlank() && !"ROLE_ADMIN".equals(userRoleHeader)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied: Deleting a user requires ROLE_ADMIN authority.");
        }

        userService.deleteUser(id);
        return ResponseEntity.ok("User account deleted successfully with ID: " + id);
    }
}
