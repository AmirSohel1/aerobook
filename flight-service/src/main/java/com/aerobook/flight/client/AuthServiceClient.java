package com.aerobook.flight.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * OpenFeign declarative HTTP client communicating with Auth Service. Used for
 * inter-service JWT validation and token introspection.
 */
@FeignClient(name = "auth-service")
public interface AuthServiceClient {

    /**
     * Validates a JWT Bearer token against Auth Service.
     *
     * @param token the HTTP Authorization header containing "Bearer <jwt>"
     * @return true if token is cryptographically valid and unexpired; false
     * otherwise
     */
    @GetMapping("/api/auth/validate")
    Boolean validateToken(
            @RequestHeader("Authorization") String token);
}
