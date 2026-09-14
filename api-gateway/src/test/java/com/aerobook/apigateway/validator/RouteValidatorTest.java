package com.aerobook.apigateway.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * ============================================================================
 * Gateway Route Validator Unit Tests
 * ============================================================================
 *
 * Verifies that {@link RouteValidator} correctly classifies request paths into
 * public bypass routes (no authentication required) versus secured business
 * routes (JWT authentication strictly enforced).
 *
 * @author Aerobook Platform Engineering
 */
class RouteValidatorTest {

    private RouteValidator routeValidator;

    /**
     * Initializes a fresh RouteValidator instance before each test.
     */
    @BeforeEach
    void setUp() {
        routeValidator = new RouteValidator();
    }

    /**
     * Verifies that public auth endpoints (/register, /login, /refresh-token)
     * bypass security.
     */
    @Test
    @DisplayName("Verify public auth endpoints bypass JWT authentication")
    void publicAuthEndpoints_AreNotSecured() {
        var registerReq = MockServerHttpRequest.get("/api/auth/register").build();
        var loginReq = MockServerHttpRequest.get("/api/auth/login").build();
        var refreshReq = MockServerHttpRequest.get("/api/auth/refresh-token").build();

        assertThat(routeValidator.isSecured.test(registerReq)).isFalse();
        assertThat(routeValidator.isSecured.test(loginReq)).isFalse();
        assertThat(routeValidator.isSecured.test(refreshReq)).isFalse();
    }

    /**
     * Verifies that all service health probes ending with /health bypass
     * security.
     */
    @Test
    @DisplayName("Verify microservice /health probes bypass JWT authentication")
    void healthEndpoints_AreNotSecured() {
        var flightHealth = MockServerHttpRequest.get("/api/flights/health").build();
        var fareHealth = MockServerHttpRequest.get("/api/fares/health").build();
        var bookingHealth = MockServerHttpRequest.get("/api/bookings/health").build();

        assertThat(routeValidator.isSecured.test(flightHealth)).isFalse();
        assertThat(routeValidator.isSecured.test(fareHealth)).isFalse();
        assertThat(routeValidator.isSecured.test(bookingHealth)).isFalse();
    }

    /**
     * Verifies that Swagger UI web pages and OpenAPI JSON definitions bypass
     * security.
     */
    @Test
    @DisplayName("Verify Swagger UI and OpenAPI documentation endpoints bypass JWT authentication")
    void swaggerAndDocumentationEndpoints_AreNotSecured() {
        var swaggerUi = MockServerHttpRequest.get("/swagger-ui.html").build();
        var apiDocs = MockServerHttpRequest.get("/v3/api-docs").build();

        assertThat(routeValidator.isSecured.test(swaggerUi)).isFalse();
        assertThat(routeValidator.isSecured.test(apiDocs)).isFalse();
    }

    /**
     * Verifies that customer booking, fare write, flight search, and admin
     * endpoints are secured.
     */
    @Test
    @DisplayName("Verify business operations and admin routes require JWT security")
    void businessAndAdminEndpoints_AreSecured() {
        var bookingReq = MockServerHttpRequest.get("/api/bookings/1").build();
        var fareReq = MockServerHttpRequest.post("/api/fares").build();
        var flightReq = MockServerHttpRequest.get("/api/flights/search").build();
        var adminReq = MockServerHttpRequest.post("/api/admin/aircrafts").build();

        assertThat(routeValidator.isSecured.test(bookingReq)).isTrue();
        assertThat(routeValidator.isSecured.test(fareReq)).isTrue();
        assertThat(routeValidator.isSecured.test(flightReq)).isTrue();
        assertThat(routeValidator.isSecured.test(adminReq)).isTrue();
    }
}
