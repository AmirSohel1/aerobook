package com.aerobook.apigateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * ============================================================================
 * Aerobook Cloud Airline Platform - API Gateway Service (Port 8083)
 * ============================================================================
 *
 * Central entry point, reactive reverse proxy, and security firewall for the
 * Aerobook distributed microservices platform.
 *
 * <p>
 * Key Architecture Responsibilities:
 * <ul>
 * <li><b>Reverse Proxy Routing:</b> Routes external client traffic to backend
 * microservices via Spring Cloud Gateway and Netflix Eureka service discovery
 * ({@code lb://SERVICE-NAME}).</li>
 * <li><b>Centralized JWT Authentication:</b> Validates HMAC-SHA signed JWT
 * bearer tokens, extracts claims, and blocks unauthorized requests before
 * reaching downstream services.</li>
 * <li><b>Role-Based Access Control (RBAC):</b> Strictly enforces security
 * policies separating public traffic, customer access ({@code ROLE_USER}), and
 * administrative operations ({@code ROLE_ADMIN}).</li>
 * <li><b>Identity Header Propagation:</b> Mutates incoming requests to
 * propagate trusted identity headers ({@code X-User-Role},
 * {@code X-User-Email}) to internal services.</li>
 * <li><b>Aggregated Swagger UI Hub:</b> Serves centralized OpenAPI 3
 * documentation with an interactive multi-service definition dropdown
 * selector.</li>
 * </ul>
 *
 * @author Aerobook Platform Engineering
 * @version 1.0.0
 */
@SpringBootApplication
public class ApiGatewayApplication {

    /**
     * Application entry point for starting the Spring Cloud Gateway server.
     *
     * @param args command-line runtime arguments passed to the JVM
     */
    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}
