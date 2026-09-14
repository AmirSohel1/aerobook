package com.aerobook.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * ============================================================================
 * User Login Authentication Request Data Transfer Object
 * ============================================================================
 *
 * Encapsulates user email and password credentials submitted to
 * {@code /api/auth/login}.
 *
 * @author Aerobook Security Engineering
 * @version 1.0.0
 */
@Schema(description = "Request credentials to obtain JWT access token")
public class LoginRequest {

    /**
     * Registered customer or administrator email address.
     */
    @Schema(description = "Registered email address", example = "asha.khan@example.com")
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be a valid format")
    private String email;

    /**
     * Plaintext account password to be verified against BCrypt hash.
     */
    @Schema(description = "Account password", example = "Asha@12345")
    @NotBlank(message = "Password is required")
    private String password;

    /**
     * Default constructor for JSON deserialization.
     */
    public LoginRequest() {
    }

    /**
     * Parameterized constructor.
     *
     * @param email user email
     * @param password user password
     */
    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
