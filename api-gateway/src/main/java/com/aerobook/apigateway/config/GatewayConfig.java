package com.aerobook.apigateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.aerobook.apigateway.filter.AuthenticationFilter;

/**
 * ============================================================================
 * Spring Cloud Gateway Route Configuration
 * ============================================================================
 *
 * Configures the reactive routing table for the Aerobook microservice
 * ecosystem. Each route specifies:
 * <ul>
 * <li><b>Route ID:</b> Unique identifier for monitoring and metrics.</li>
 * <li><b>Path Predicate:</b> HTTP request URI patterns forwarded to the target
 * service.</li>
 * <li><b>Filter Chain:</b> Applies {@link AuthenticationFilter} for JWT
 * validation, RBAC policy enforcement, and downstream header mutation.</li>
 * <li><b>URI Destination:</b> Netflix Eureka logical service identifiers using
 * {@code lb://} client-side load balancing.</li>
 * </ul>
 *
 * @author Aerobook Platform Engineering
 */
@Configuration
public class GatewayConfig {

    /**
     * Custom JWT authentication and RBAC authorization filter factory.
     */
    private final AuthenticationFilter filter;

    /**
     * Constructs the GatewayConfig with the required authentication filter
     * dependency.
     *
     * @param filter the authentication and RBAC enforcement filter
     */
    public GatewayConfig(AuthenticationFilter filter) {
        this.filter = filter;
    }

    /**
     * Defines and registers the primary RouteLocator bean containing all
     * microservice routes and downstream documentation proxy paths.
     *
     * @param builder Spring Cloud Gateway RouteLocatorBuilder for declarative
     * routing
     * @return constructed {@link RouteLocator} with active route mappings
     */
    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {

        return builder.routes()
                // ------------------------------------------------------------
                // 1. Auth Service Route (Port 8082)
                // ------------------------------------------------------------
                // Handles customer registration, admin setup key verification,
                // credential login, JWT token refresh, and credential oversight.
                .route("auth-service",
                        r -> r.path("/api/auth/**")
                                .filters(f -> f.filter(filter.apply(
                                new AuthenticationFilter.Config())))
                                .uri("lb://AUTH-SERVICE"))
                // ------------------------------------------------------------
                // 2. User Service Route (Port 8084)
                // ------------------------------------------------------------
                // Manages customer account profiles, demographics, contact info,
                // and administrative user directory operations.
                .route("user-service",
                        r -> r.path("/api/v1/users", "/api/v1/users/**")
                                .filters(f -> f.filter(filter.apply(
                                new AuthenticationFilter.Config())))
                                .uri("lb://USER-SERVICE"))
                // ------------------------------------------------------------
                // 3. Flight Service Route (Port 8087)
                // ------------------------------------------------------------
                // Supports flight route search (source/destination), scheduled flight
                // catalog, fleet aircraft management, and admin flight scheduling.
                .route("flight-service",
                        r -> r.path("/api/flights", "/api/flights/**",
                                "/api/admin/flights", "/api/admin/flights/**",
                                "/api/admin/aircrafts", "/api/admin/aircrafts/**")
                                .filters(f -> f.filter(filter.apply(
                                new AuthenticationFilter.Config())))
                                .uri("lb://FLIGHT-SERVICE"))
                // ------------------------------------------------------------
                // 4. Fare Service Route (Port 8089)
                // ------------------------------------------------------------
                // Handles multi-tier cabin pricing (Economy, Business, First Class),
                // GST tax calculation, promotional discounts, and pricing administration.
                .route("fare-service",
                        r -> r.path("/api/fares", "/api/fares/**")
                                .filters(f -> f.filter(filter.apply(
                                new AuthenticationFilter.Config())))
                                .uri("lb://FARE-SERVICE"))
                // ------------------------------------------------------------
                // 5. Booking Service Route (Port 8088)
                // ------------------------------------------------------------
                // Processes flight reservations, generates unique 8-character PNRs,
                // validates flight date/seat availability via OpenFeign, and publishes
                // asynchronous BOOKING_CREATED events to RabbitMQ.
                .route("booking-service",
                        r -> r.path("/api/bookings", "/api/bookings/**")
                                .filters(f -> f.filter(filter.apply(
                                new AuthenticationFilter.Config())))
                                .uri("lb://BOOKING-SERVICE"))
                // ------------------------------------------------------------
                // 6. Check-In Service Route (Port 8090)
                // ------------------------------------------------------------
                // Performs passenger airport check-in, seat assignment, and barcode
                // boarding pass generation (format: BP-1-12A).
                .route("check-in-service",
                        r -> r.path("/api/check-ins", "/api/check-ins/**")
                                .filters(f -> f.filter(filter.apply(
                                new AuthenticationFilter.Config())))
                                .uri("lb://CHECK-IN-SERVICE"))
                // ------------------------------------------------------------
                // 7. Notification Service Route (Port 8091)
                // ------------------------------------------------------------
                // Dispatches real-time passenger notices, gate change updates,
                // and operational flight delay broadcasts.
                .route("notification-service",
                        r -> r.path("/api/notifications", "/api/notifications/**")
                                .filters(f -> f.filter(filter.apply(
                                new AuthenticationFilter.Config())))
                                .uri("lb://NOTIFICATION-SERVICE"))
                // ------------------------------------------------------------
                // Downstream Swagger / OpenAPI Documentation Proxy Routes
                // ------------------------------------------------------------
                // Allows direct retrieval of downstream microservice OpenAPI specs
                // by stripping the service prefix and rewriting to /v3/api-docs.
                .route("auth-service-docs",
                        r -> r.path("/v3/api-docs/auth-service", "/v3/api-docs/auth-service/**")
                                .filters(f -> f.rewritePath("/v3/api-docs/auth-service(?<segment>.*)", "/v3/api-docs${segment}"))
                                .uri("lb://AUTH-SERVICE"))
                .route("user-service-docs",
                        r -> r.path("/v3/api-docs/user-service", "/v3/api-docs/user-service/**")
                                .filters(f -> f.rewritePath("/v3/api-docs/user-service(?<segment>.*)", "/v3/api-docs${segment}"))
                                .uri("lb://USER-SERVICE"))
                .route("flight-service-docs",
                        r -> r.path("/v3/api-docs/flight-service", "/v3/api-docs/flight-service/**")
                                .filters(f -> f.rewritePath("/v3/api-docs/flight-service(?<segment>.*)", "/v3/api-docs${segment}"))
                                .uri("lb://FLIGHT-SERVICE"))
                .route("fare-service-docs",
                        r -> r.path("/v3/api-docs/fare-service", "/v3/api-docs/fare-service/**")
                                .filters(f -> f.rewritePath("/v3/api-docs/fare-service(?<segment>.*)", "/v3/api-docs${segment}"))
                                .uri("lb://FARE-SERVICE"))
                .route("booking-service-docs",
                        r -> r.path("/v3/api-docs/booking-service", "/v3/api-docs/booking-service/**")
                                .filters(f -> f.rewritePath("/v3/api-docs/booking-service(?<segment>.*)", "/v3/api-docs${segment}"))
                                .uri("lb://BOOKING-SERVICE"))
                .route("check-in-service-docs",
                        r -> r.path("/v3/api-docs/check-in-service", "/v3/api-docs/check-in-service/**")
                                .filters(f -> f.rewritePath("/v3/api-docs/check-in-service(?<segment>.*)", "/v3/api-docs${segment}"))
                                .uri("lb://CHECK-IN-SERVICE"))
                .route("notification-service-docs",
                        r -> r.path("/v3/api-docs/notification-service", "/v3/api-docs/notification-service/**")
                                .filters(f -> f.rewritePath("/v3/api-docs/notification-service(?<segment>.*)", "/v3/api-docs${segment}"))
                                .uri("lb://NOTIFICATION-SERVICE"))
                .build();
    }
}
