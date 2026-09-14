package com.aerobook.apigateway.validator;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

/**
 * ============================================================================
 * Gateway Security Route Classification Validator
 * ============================================================================
 *
 * Evaluates incoming reactive HTTP request paths against security bypass lists
 * to determine whether JWT authentication and role validation are required.
 *
 * <p>
 * Bypass Rules:
 * <ul>
 * <li><b>Public Auth Paths:</b> User registration, admin bootstrap
 * registration, credential login, and refresh token exchange.</li>
 * <li><b>Platform Diagnostics:</b> Central gateway topology, health, catalog,
 * and demo endpoints under {@code /api/gateway/**}.</li>
 * <li><b>Liveness Probes:</b> Any microservice endpoint ending with
 * {@code /health} (e.g. {@code /api/flights/health},
 * {@code /api/bookings/health}).</li>
 * <li><b>API Documentation:</b> Swagger UI static web assets and OpenAPI JSON
 * definitions
 * ({@code /swagger-ui/**}, {@code /v3/api-docs/**}, {@code /webjars/**}).</li>
 * </ul>
 *
 * @author Aerobook Platform Engineering
 * @version 1.0.0
 */
@Component
public class RouteValidator {

    /**
     * Exact path prefixes that are publicly accessible and bypass JWT
     * authentication.
     */
    public static final List<String> openApiEndpoints = List.of(
            "/api/auth/register",
            "/api/auth/register-admin",
            "/api/auth/login",
            "/api/auth/refresh-token",
            "/api/gateway",
            "/v3/api-docs",
            "/swagger-ui",
            "/swagger-ui.html",
            "/webjars"
    );

    /**
     * Reactive predicate evaluating whether an incoming request requires
     * security validation. Returns {@code false} (bypassed) for health probes
     * and whitelisted public endpoints; returns {@code true} (secured) for all
     * business and administrative operations.
     */
    public Predicate<ServerHttpRequest> isSecured
            = request -> {
                String path = request.getURI().getPath();

                // All microservice health probes are publicly accessible
                if (path.endsWith("/health")) {
                    return false;
                }

                // Match against whitelisted public path prefixes
                return openApiEndpoints.stream().noneMatch(path::startsWith);
            };
}
