package com.aerobook.auth.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

import com.aerobook.auth.dto.UserResponse;
import com.aerobook.auth.dto.UserCreateRequest;

/**
 * ============================================================================
 * Declarative OpenFeign HTTP Client for User Profile Service (Port 8084)
 * ============================================================================
 *
 * Synchronously communicates with {@code user-service} via Eureka service
 * discovery to replicate account profile details (first name, last name, phone,
 * birth date, nationality, and assigned role) during user registration and
 * administrator role updates.
 *
 * @author Aerobook Security Engineering
 * @version 1.0.0
 */
@FeignClient(
        name = "user-service"
)
public interface UserClient {

    /**
     * Creates a new user profile record in user-service upon registration in
     * auth-service.
     *
     * @param request customer demographic and profile payload
     * @return {@link UserResponse} containing generated primary user account ID
     */
    @PostMapping("/api/v1/users")
    UserResponse createUser(
            @RequestBody UserCreateRequest request
    );

    /**
     * Updates profile and authority role of an existing account in
     * user-service.
     *
     * @param id target user primary identifier
     * @param request updated user profile payload
     * @return {@link UserResponse} containing updated profile confirmation
     */
    @PutMapping("/api/v1/users/{id}")
    UserResponse updateUser(
            @PathVariable("id") Long id,
            @RequestBody UserCreateRequest request
    );
}
