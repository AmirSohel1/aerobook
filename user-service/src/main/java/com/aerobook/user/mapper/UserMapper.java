package com.aerobook.user.mapper;

import com.aerobook.user.dto.request.UserRequest;
import com.aerobook.user.dto.response.UserResponse;
import com.aerobook.user.entity.Role;
import com.aerobook.user.entity.UserEntity;

/**
 * ============================================================================
 * User Model / DTO Bidirectional Transformation Mapper
 * ============================================================================
 *
 * Stateless utility class providing transformation routines between database
 * {@link UserEntity} domain objects and external API {@link UserRequest} /
 * {@link UserResponse} Data Transfer Objects.
 *
 * @author Aerobook Platform Engineering
 * @version 1.0.0
 */
public final class UserMapper {

    /**
     * Private constructor enforcing utility class design pattern.
     */
    private UserMapper() {
    }

    /**
     * Converts an incoming {@link UserRequest} DTO into a new
     * {@link UserEntity}. Safely normalizes optional attributes and defaults
     * missing roles to {@link Role#ROLE_USER}.
     *
     * @param request the incoming user payload, or {@code null}
     * @return populated {@link UserEntity} or {@code null} if input is null
     */
    public static UserEntity toEntity(UserRequest request) {
        if (request == null) {
            return null;
        }

        UserEntity user = new UserEntity();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setDateOfBirth(request.getDateOfBirth());
        user.setNationality(request.getNationality());
        user.setRole(parseRole(request.getRole()));

        return user;
    }

    /**
     * Transforms a database {@link UserEntity} record into a client-facing
     * {@link UserResponse} DTO.
     *
     * @param user the persisted user domain entity, or {@code null}
     * @return transformed {@link UserResponse} or {@code null} if input is null
     */
    public static UserResponse toResponse(UserEntity user) {
        if (user == null) {
            return null;
        }
        return new UserResponse(
                user.getUserId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getDateOfBirth(),
                user.getNationality(),
                user.getRole() == null ? Role.ROLE_USER.name() : user.getRole().name()
        );
    }

    /**
     * Parses a role string into a valid {@link Role} enum constant with safe
     * fallback.
     *
     * @param role string role name (e.g. "ROLE_ADMIN")
     * @return parsed {@link Role} enum value, defaulting to
     * {@link Role#ROLE_USER} if invalid or empty
     */
    private static Role parseRole(String role) {
        if (role == null || role.isBlank()) {
            return Role.ROLE_USER;
        }
        try {
            return Role.valueOf(role.trim().toUpperCase());
        } catch (IllegalArgumentException exception) {
            return Role.ROLE_USER;
        }
    }
}
