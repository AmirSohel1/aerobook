package com.aerobook.auth.dto;

/**
 * ============================================================================
 * User Service Creation Response Data Transfer Object
 * ============================================================================
 *
 * Received from User Service upon successfully creating a user profile.
 * Contains the generated primary database identifier ({@code userId}).
 *
 * @author Aerobook Security Engineering
 * @version 1.0.0
 */
public class UserResponse {

    /**
     * Generated user account primary database identifier.
     */
    private Long userId;

    /**
     * Registered user email.
     */
    private String email;

    public UserResponse() {
    }

    public UserResponse(Long userId, String email) {
        this.userId = userId;
        this.email = email;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
