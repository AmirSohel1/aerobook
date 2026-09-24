package com.aerobook.apigateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import com.aerobook.apigateway.util.JwtUtil;
import com.aerobook.apigateway.validator.RouteValidator;

/**
 * ============================================================================
 * Central Authentication & Role-Based Access Control (RBAC) Gateway Filter
 * ============================================================================
 *
 * Intercepts all traffic routed to backend microservices and applies strict
 * security rules:
 * <ol>
 * <li><b>Route Classification:</b> Checks if the target URI is secured using
 * {@link RouteValidator}. Public endpoints (e.g. login, register, health
 * checks, Swagger UI) bypass authentication.</li>
 * <li><b>Authorization Header Validation:</b> Verifies the presence of the
 * {@code Authorization} header and strips the {@code Bearer } token
 * prefix.</li>
 * <li><b>Cryptographic Signature & Expiry Check:</b> Calls
 * {@link JwtUtil#validateToken(String)} to verify the token against the
 * HMAC-SHA signing key. Invalid/expired tokens return 401 Unauthorized.</li>
 * <li><b>Claims Extraction:</b> Extracts authenticated user identity
 * ({@code email}) and security role ({@code ROLE_USER} or
 * {@code ROLE_ADMIN}).</li>
 * <li><b>RBAC Policy Enforcement:</b> Blocks non-admin users with 403 Forbidden
 * from accessing:
 * <ul>
 * <li>Dedicated admin paths: {@code /api/admin/**} (aircraft fleet, flight
 * schedules)</li>
 * <li>Administrative user operations:
 * {@code GET/POST/DELETE /api/v1/users}</li>
 * <li>Fare configuration write operations:
 * {@code POST/PUT/DELETE /api/fares/**}</li>
 * <li>Global airline reservation audit: {@code GET /api/bookings}</li>
 * <li>Global airport check-in audit: {@code GET /api/check-ins}</li>
 * <li>Credential audit & role elevation: {@code /api/auth/credentials/**}</li>
 * </ul>
 * </li>
 * <li><b>Downstream Identity Propagation:</b> Mutates the reactive request to
 * append trusted {@code X-User-Role} and {@code X-User-Email} headers for
 * downstream consumption.</li>
 * </ol>
 *
 * @author Aerobook Platform Engineering
 * @version 1.0.0
 */
@Component
public class AuthenticationFilter
        extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    private static final Logger log = LoggerFactory.getLogger(AuthenticationFilter.class);

    /**
     * Validator to identify whether an incoming request path requires JWT
     * authentication.
     */
    private final RouteValidator validator;

    /**
     * Utility component for verifying JWT signatures and extracting user
     * claims.
     */
    private final JwtUtil jwtUtil;

    /**
     * Constructs the AuthenticationFilter with required validator and JWT
     * utility.
     *
     * @param validator route validation component
     * @param jwtUtil token verification and claims extraction helper
     */
    public AuthenticationFilter(RouteValidator validator, JwtUtil jwtUtil) {
        super(Config.class);
        this.validator = validator;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Applies the reactive filter logic to the Spring Cloud Gateway filter
     * chain.
     *
     * @param config filter configuration properties
     * @return reactive {@link GatewayFilter} lambda
     */
    @Override
    public GatewayFilter apply(Config config) {

        return (exchange, chain) -> {

            // Step 1: Check if the incoming request path requires authentication
            if (validator.isSecured.test(exchange.getRequest())) {

                // Step 2: Ensure Authorization header is present
                if (!exchange.getRequest()
                        .getHeaders()
                        .containsKey(HttpHeaders.AUTHORIZATION)) {
                    log.warn("Unauthorized ingress attempt to secured path '{}': Missing Authorization header", exchange.getRequest().getURI().getPath());
                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                    return exchange.getResponse().setComplete();
                }

                String authHeader = exchange.getRequest()
                        .getHeaders()
                        .getFirst(HttpHeaders.AUTHORIZATION);

                // Step 3: Strip "Bearer " scheme prefix
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    authHeader = authHeader.substring(7);
                }

                // Step 4: Cryptographically validate token signature and expiration
                if (!jwtUtil.validateToken(authHeader)) {
                    log.warn("Unauthorized ingress attempt to secured path '{}': Invalid or expired JWT token", exchange.getRequest().getURI().getPath());
                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                    return exchange.getResponse().setComplete();
                }

                String path = exchange.getRequest().getURI().getPath();
                HttpMethod method = exchange.getRequest().getMethod();

                // Step 5: Extract user role and subject (email) claims
                String userRole = jwtUtil.extractAllClaims(authHeader).get("role", String.class);
                String userEmail = jwtUtil.extractUsername(authHeader);
                boolean isAdmin = "ROLE_ADMIN".equals(userRole);
                boolean isStaff = "ROLE_STAFF".equals(userRole);

                // Step 6: Role-Based Access Control (RBAC) Enforcement Rules:
                // Rule 1: Dedicated admin routes (/api/admin/**) strictly require ROLE_ADMIN
                boolean isAdminPath = path.startsWith("/api/admin/");

                // Rule 2: User Service administrative endpoints:
                //         - GET /api/v1/users (list all customer accounts)
                //         - POST /api/v1/users (direct user creation)
                //         - DELETE /api/v1/users/** (account deletion)
                boolean isUserList = path.equals("/api/v1/users") && HttpMethod.GET.equals(method);
                boolean isUserCreate = path.equals("/api/v1/users") && HttpMethod.POST.equals(method);
                boolean isUserDelete = path.startsWith("/api/v1/users") && HttpMethod.DELETE.equals(method);
                boolean isUserAdminOnly = isUserList || isUserCreate || isUserDelete;

                // Rule 3: Fare Service write operations (configure/update/delete fare) require ROLE_ADMIN
                boolean isFareWrite = path.startsWith("/api/fares")
                        && (method != null && !HttpMethod.GET.equals(method));

                // Rule 4: Booking Service: GET /api/bookings (list all bookings across airline) accessible by ROLE_ADMIN and ROLE_STAFF
                boolean isBookingListAll = path.equals("/api/bookings") && HttpMethod.GET.equals(method);

                // Rule 5: Check-In Service: GET /api/check-ins (audit all check-ins) accessible by ROLE_ADMIN and ROLE_STAFF
                boolean isCheckInListAll = path.equals("/api/check-ins") && HttpMethod.GET.equals(method);

                // Rule 6: Auth Service: /api/auth/credentials/** (credential audit & role management) requires ROLE_ADMIN
                boolean isAuthAdmin = path.startsWith("/api/auth/credentials");

                // Check Admin-only operations
                if ((isAdminPath || isUserAdminOnly || isFareWrite || isAuthAdmin) && !isAdmin) {
                    log.warn("Forbidden access attempt to '{}' [{}] by user '{}' (Role: '{}')", path, method, userEmail, userRole);
                    exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                    return exchange.getResponse().setComplete();
                }

                // Check Operations requiring Staff or Admin authority
                if ((isBookingListAll || isCheckInListAll) && !(isAdmin || isStaff)) {
                    log.warn("Forbidden access attempt to operational audit '{}' [{}] by user '{}' (Role: '{}')", path, method, userEmail, userRole);
                    exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                    return exchange.getResponse().setComplete();
                }

                log.debug("Authorized request to '{}' [{}] for user '{}' (Role: '{}')", path, method, userEmail, userRole);

                // Step 7: Mutate request to propagate verified identity headers downstream
                ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                        .header("X-User-Role", userRole != null ? userRole : "")
                        .header("X-User-Email", userEmail != null ? userEmail : "")
                        .build();

                return chain.filter(exchange.mutate().request(mutatedRequest).build());
            }

            // Public path bypassed: proceed through filter chain without authentication
            return chain.filter(exchange);
        };
    }

    /**
     * Configuration class for AuthenticationFilter parameters (supports future
     * expansion).
     */
    public static class Config {
    }
}
