package com.aerobook.apigateway.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * ============================================================================
 * Gateway Platform Hub & Overview Controller (Port 8083)
 * ============================================================================
 *
 * Exposes core platform diagnostic endpoints:
 * <ul>
 * <li><b>Health Probe:</b> Operational status and reactive engine
 * verification.</li>
 * <li><b>Architecture Directory:</b> Microservice topology, ports, and database
 * mapping.</li>
 * <li><b>Endpoints Catalog:</b> Complete catalog of all platform endpoints with
 * roles.</li>
 * <li><b>RBAC Security Matrix:</b> Explicit Role-Based Access Control policy
 * listing.</li>
 * <li><b>Presentation Script:</b> Step-by-step live evaluation script for
 * evaluators.</li>
 * </ul>
 *
 * @author Aerobook Platform Engineering
 * @version 1.0.0
 */
@Tag(name = "API Gateway Platform Hub", description = "Central routing hub, microservice topology, master endpoint catalog, and presentation guide")
@RestController
@RequestMapping("/api/gateway")
public class GatewayOverviewController {

    /**
     * Operational health and liveness probe for the API Gateway.
     *
     * @return {@link ResponseEntity} containing gateway status, port, engine,
     * and timestamp
     */
    @Operation(summary = "Get API Gateway Health Status", description = "Verifies the operational status of the central Spring Cloud Gateway.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "API Gateway is healthy and running",
                content = @Content(mediaType = "application/json",
                        examples = @ExampleObject(value = "{\"status\":\"UP\",\"service\":\"Aerobook API Gateway\",\"port\":8083,\"eurekaRegistered\":true}")))
    })
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> getHealth() {
        Map<String, Object> health = new LinkedHashMap<>();
        health.put("status", "UP");
        health.put("service", "Aerobook API Gateway");
        health.put("port", 8083);
        health.put("routingEngine", "Spring Cloud Gateway WebFlux Reactive");
        health.put("eurekaRegistered", true);
        health.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.ok(health);
    }

    /**
     * Returns architectural topology and metadata for all 6 microservices in
     * the platform.
     *
     * @return {@link ResponseEntity} containing microservice topology, ports,
     * databases, and responsibilities
     */
    @Operation(summary = "Get Microservices Architecture Directory", description = "Returns complete architectural mapping of all microservices, their ports, database instances, and key capabilities.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Microservice directory retrieved successfully")
    })
    @GetMapping("/services")
    public ResponseEntity<Map<String, Object>> getServicesDirectory() {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("architecture", "Aerobook Distributed Cloud Architecture");
        response.put("gatewayPort", 8083);

        List<Map<String, Object>> services = List.of(
                Map.of(
                        "name", "Auth Service",
                        "port", 8082,
                        "database", "aerobook_auth_db (MySQL)",
                        "responsibility", "User registration, password encryption (BCrypt), JWT token lifecycle, master admin setup, and role management",
                        "basePath", "/api/auth/**"
                ),
                Map.of(
                        "name", "User Service",
                        "port", 8084,
                        "database", "aerobook_user_db (MySQL)",
                        "responsibility", "Customer account profiles, demographics, administrative user management",
                        "basePath", "/api/v1/users/**"
                ),
                Map.of(
                        "name", "Flight Service",
                        "port", 8087,
                        "database", "aerobook_flight_db (MySQL)",
                        "responsibility", "Flight schedule search (source & destination), admin flight scheduling, aircraft inventory fleet",
                        "basePath", "/api/flights/**, /api/admin/**"
                ),
                Map.of(
                        "name", "Fare Service",
                        "port", 8089,
                        "database", "aerobook_fare_db (MySQL)",
                        "responsibility", "Multi-tier cabin pricing (Economy, Business, First Class), tax calculation, promotional discounts",
                        "basePath", "/api/fares/**"
                ),
                Map.of(
                        "name", "Booking Service",
                        "port", 8088,
                        "database", "aerobook_booking_db (MySQL)",
                        "responsibility", "Passenger reservations, PNR generation, Feign flight client, date & seat validations, RabbitMQ BOOKING_CREATED events",
                        "basePath", "/api/bookings/**"
                ),
                Map.of(
                        "name", "Check-In Service",
                        "port", 8090,
                        "database", "aerobook_checkin_db (MySQL)",
                        "responsibility", "Passenger flight check-in, boarding pass generation (BP-1-12A), seat assignment, boarding confirmation timestamps",
                        "basePath", "/api/check-ins/**"
                )
        );

        response.put("services", services);
        return ResponseEntity.ok(response);
    }

    /**
     * Master endpoint catalog enumerating all HTTP routes across all platform
     * microservices.
     *
     * @return {@link ResponseEntity} containing list of endpoints with methods,
     * roles, and descriptions
     */
    @Operation(summary = "Get Master Platform API Catalog", description = "Comprehensive catalog listing all API endpoints across all microservices, their HTTP methods, required roles, and descriptions.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Master API endpoint catalog retrieved successfully")
    })
    @GetMapping("/endpoints")
    public ResponseEntity<Map<String, Object>> getEndpointsCatalog() {
        Map<String, Object> catalog = new LinkedHashMap<>();
        catalog.put("platform", "Aerobook Cloud Airline Platform");
        catalog.put("totalServices", 6);

        List<Map<String, String>> endpoints = List.of(
                // 1. Auth Service Endpoints
                Map.of("service", "auth-service (:8082)", "method", "GET", "path", "/api/auth/health", "role", "PUBLIC", "description", "Service health check"),
                Map.of("service", "auth-service (:8082)", "method", "POST", "path", "/api/auth/register", "role", "PUBLIC", "description", "Customer registration (ROLE_USER)"),
                Map.of("service", "auth-service (:8082)", "method", "POST", "path", "/api/auth/register-admin", "role", "X-Admin-Setup-Key", "description", "Admin setup / role elevation"),
                Map.of("service", "auth-service (:8082)", "method", "POST", "path", "/api/auth/login", "role", "PUBLIC", "description", "User login & JWT token issuance"),
                Map.of("service", "auth-service (:8082)", "method", "POST", "path", "/api/auth/refresh-token", "role", "PUBLIC", "description", "Exchange refresh token for access token"),
                Map.of("service", "auth-service (:8082)", "method", "GET", "path", "/api/auth/credentials", "role", "ROLE_ADMIN", "description", "List all platform credentials and roles"),
                Map.of("service", "auth-service (:8082)", "method", "PUT", "path", "/api/auth/credentials/{userId}/role", "role", "ROLE_ADMIN", "description", "Promote/demote user authority role"),
                // 2. User Service Endpoints
                Map.of("service", "user-service (:8084)", "method", "GET", "path", "/api/v1/users/health", "role", "PUBLIC", "description", "Service health check"),
                Map.of("service", "user-service (:8084)", "method", "GET", "path", "/api/v1/users/{id}", "role", "ROLE_USER, ROLE_ADMIN", "description", "Get user profile by ID"),
                Map.of("service", "user-service (:8084)", "method", "GET", "path", "/api/v1/users/email/{email}", "role", "ROLE_USER, ROLE_ADMIN", "description", "Get user profile by email"),
                Map.of("service", "user-service (:8084)", "method", "PUT", "path", "/api/v1/users/{id}", "role", "ROLE_USER, ROLE_ADMIN", "description", "Update user profile details"),
                Map.of("service", "user-service (:8084)", "method", "GET", "path", "/api/v1/users", "role", "ROLE_ADMIN", "description", "List all registered user accounts"),
                Map.of("service", "user-service (:8084)", "method", "POST", "path", "/api/v1/users", "role", "ROLE_ADMIN", "description", "Create user account directly"),
                Map.of("service", "user-service (:8084)", "method", "DELETE", "path", "/api/v1/users/{id}", "role", "ROLE_ADMIN", "description", "Delete user account"),
                // 3. Flight Service Endpoints
                Map.of("service", "flight-service (:8087)", "method", "GET", "path", "/api/flights/health", "role", "PUBLIC", "description", "Service health check"),
                Map.of("service", "flight-service (:8087)", "method", "GET", "path", "/api/flights/search", "role", "ROLE_USER, ROLE_ADMIN", "description", "Search flights by source and destination"),
                Map.of("service", "flight-service (:8087)", "method", "GET", "path", "/api/flights", "role", "ROLE_USER, ROLE_ADMIN", "description", "Browse complete flight catalog"),
                Map.of("service", "flight-service (:8087)", "method", "GET", "path", "/api/flights/{id}", "role", "ROLE_USER, ROLE_ADMIN", "description", "Get flight details and available seats"),
                Map.of("service", "flight-service (:8087)", "method", "POST", "path", "/api/admin/aircrafts", "role", "ROLE_ADMIN", "description", "Register new aircraft model into fleet"),
                Map.of("service", "flight-service (:8087)", "method", "GET", "path", "/api/admin/aircrafts", "role", "ROLE_ADMIN", "description", "List all aircraft in fleet"),
                Map.of("service", "flight-service (:8087)", "method", "GET/DELETE", "path", "/api/admin/aircrafts/{id}", "role", "ROLE_ADMIN", "description", "Inspect or remove aircraft from fleet"),
                Map.of("service", "flight-service (:8087)", "method", "POST", "path", "/api/admin/flights", "role", "ROLE_ADMIN", "description", "Schedule new commercial flight"),
                Map.of("service", "flight-service (:8087)", "method", "PUT/DELETE", "path", "/api/admin/flights/{id}", "role", "ROLE_ADMIN", "description", "Reschedule or cancel flight"),
                Map.of("service", "flight-service (:8087)", "method", "GET", "path", "/api/admin/flights", "role", "ROLE_ADMIN", "description", "Admin view of all flight schedules"),
                // 4. Fare Service Endpoints
                Map.of("service", "fare-service (:8089)", "method", "GET", "path", "/api/fares/health", "role", "PUBLIC", "description", "Service health check"),
                Map.of("service", "fare-service (:8089)", "method", "GET", "path", "/api/fares/{id}", "role", "ROLE_USER, ROLE_ADMIN", "description", "Get fare details by ID"),
                Map.of("service", "fare-service (:8089)", "method", "GET", "path", "/api/fares/flight/{flightId}", "role", "ROLE_USER, ROLE_ADMIN", "description", "Get fare configuration for flight"),
                Map.of("service", "fare-service (:8089)", "method", "GET", "path", "/api/fares", "role", "ROLE_USER, ROLE_ADMIN", "description", "List all flight fares"),
                Map.of("service", "fare-service (:8089)", "method", "POST", "path", "/api/fares", "role", "ROLE_ADMIN", "description", "Configure tiered fare for flight"),
                Map.of("service", "fare-service (:8089)", "method", "PUT", "path", "/api/fares/{id}", "role", "ROLE_ADMIN", "description", "Update fare prices, discounts, or taxes"),
                Map.of("service", "fare-service (:8089)", "method", "DELETE", "path", "/api/fares/{id}", "role", "ROLE_ADMIN", "description", "Delete fare record"),
                // 5. Booking Service Endpoints
                Map.of("service", "booking-service (:8088)", "method", "GET", "path", "/api/bookings/health", "role", "PUBLIC", "description", "Service health check"),
                Map.of("service", "booking-service (:8088)", "method", "POST", "path", "/api/bookings", "role", "ROLE_USER, ROLE_ADMIN", "description", "Reserve flight seats (Date, status, seat validation)"),
                Map.of("service", "booking-service (:8088)", "method", "GET", "path", "/api/bookings/{id}", "role", "ROLE_USER, ROLE_ADMIN", "description", "Get booking by ID"),
                Map.of("service", "booking-service (:8088)", "method", "GET", "path", "/api/bookings/pnr/{pnr}", "role", "ROLE_USER, ROLE_ADMIN", "description", "Lookup booking by PNR"),
                Map.of("service", "booking-service (:8088)", "method", "GET", "path", "/api/bookings/user/{userId}", "role", "ROLE_USER, ROLE_ADMIN", "description", "List bookings for user"),
                Map.of("service", "booking-service (:8088)", "method", "GET", "path", "/api/bookings", "role", "ROLE_ADMIN", "description", "List all bookings across airline"),
                Map.of("service", "booking-service (:8088)", "method", "DELETE", "path", "/api/bookings/{id}", "role", "ROLE_USER, ROLE_ADMIN", "description", "Cancel booking (safeguarded)"),
                // 6. Check-In Service Endpoints
                Map.of("service", "check-in-service (:8090)", "method", "GET", "path", "/api/check-ins/health", "role", "PUBLIC", "description", "Service health check"),
                Map.of("service", "check-in-service (:8090)", "method", "POST", "path", "/api/check-ins", "role", "ROLE_USER, ROLE_ADMIN", "description", "Check in passenger & generate boarding pass"),
                Map.of("service", "check-in-service (:8090)", "method", "GET", "path", "/api/check-ins/booking/{bookingId}", "role", "ROLE_USER, ROLE_ADMIN", "description", "Get check-in by booking ID"),
                Map.of("service", "check-in-service (:8090)", "method", "GET", "path", "/api/check-ins/{id}", "role", "ROLE_USER, ROLE_ADMIN", "description", "Get check-in by ID"),
                Map.of("service", "check-in-service (:8090)", "method", "GET", "path", "/api/check-ins", "role", "ROLE_ADMIN", "description", "List all passenger check-ins")
        );

        catalog.put("endpoints", endpoints);
        return ResponseEntity.ok(catalog);
    }

    /**
     * Returns structured Role-Based Access Control (RBAC) security mapping.
     * Clearly separates Public, Customer (ROLE_USER), and Administrator
     * (ROLE_ADMIN) access policies.
     *
     * @return {@link ResponseEntity} containing categorized security authority
     * matrices
     */
    @Operation(summary = "Get Platform RBAC Matrix", description = "Returns complete Role-Based Access Control security policy mapping for Public, User, and Admin authorities.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "RBAC Matrix retrieved successfully")
    })
    @GetMapping("/rbac-matrix")
    public ResponseEntity<Map<String, Object>> getRbacMatrix() {
        Map<String, Object> rbac = new LinkedHashMap<>();
        rbac.put("securityModel", "JWT Bearer Token RBAC with Claims Verification");
        rbac.put("rolesSupported", List.of("ROLE_USER", "ROLE_ADMIN"));

        rbac.put("publicEndpoints", List.of(
                "POST /api/auth/register (Customer Registration)",
                "POST /api/auth/login (Authentication)",
                "POST /api/auth/refresh-token (Token Refresh)",
                "POST /api/auth/register-admin (Guarded by X-Admin-Setup-Key)",
                "GET /api/gateway/** (Platform Topology & Health)",
                "GET /api/*/health (Microservice Liveness Probes)",
                "GET /swagger-ui/**, /v3/api-docs/** (Documentation)"
        ));

        rbac.put("customerEndpoints", List.of(
                "GET /api/flights/search, GET /api/flights, GET /api/flights/{id}",
                "POST /api/bookings, GET /api/bookings/{id}, GET /api/bookings/pnr/{pnr}, GET /api/bookings/user/{id}, DELETE /api/bookings/{id}",
                "POST /api/check-ins, GET /api/check-ins/booking/{id}, GET /api/check-ins/{id}",
                "GET /api/fares/**",
                "GET /api/v1/users/{id}, GET /api/v1/users/email/{email}, PUT /api/v1/users/{id}"
        ));

        rbac.put("adminOnlyEndpoints", List.of(
                "GET /api/auth/credentials, PUT /api/auth/credentials/{userId}/role",
                "GET /api/v1/users, POST /api/v1/users, DELETE /api/v1/users/{id}",
                "POST /api/admin/aircrafts, GET /api/admin/aircrafts, DELETE /api/admin/aircrafts/{id}",
                "POST /api/admin/flights, PUT /api/admin/flights/{id}, DELETE /api/admin/flights/{id}, GET /api/admin/flights",
                "POST /api/fares, PUT /api/fares/{id}, DELETE /api/fares/{id}",
                "GET /api/bookings (List all bookings across airline)",
                "GET /api/check-ins (List all passenger check-in confirmations)"
        ));

        return ResponseEntity.ok(rbac);
    }

    /**
     * Teacher / Evaluator demonstration script outlining step-by-step
     * presentation instructions.
     *
     * @return {@link ResponseEntity} containing live evaluation walkthrough
     * steps
     */
    @Operation(summary = "Get Teacher Presentation Demo Script", description = "Provides a structured, sequential presentation script for demonstrating the Aerobook system during final sprint evaluation.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Presentation script retrieved successfully")
    })
    @GetMapping("/presentation-demo")
    public ResponseEntity<Map<String, Object>> getPresentationGuide() {
        Map<String, Object> demo = new LinkedHashMap<>();
        demo.put("title", "Aerobook Live Demo Presentation Checklist");
        demo.put("gatewaySwaggerUrl", "http://localhost:8083/swagger-ui.html");
        demo.put("steps", List.of(
                "Step 1: Open API Gateway Swagger UI at http://localhost:8083/swagger-ui.html",
                "Step 2: Demonstrate Swagger Service Dropdown (shows all 6 microservices aggregated in one central hub)",
                "Step 3: Select '1. Auth Service' -> Execute POST /api/auth/register-admin with X-Admin-Setup-Key: AerobookAdminSetup2026",
                "Step 4: Copy accessToken -> Click green 'Authorize' button -> Paste token (Persistent JWT active)",
                "Step 5: Select '3. Flight Service' -> Execute POST /api/admin/aircrafts (Register A320 Airbus, capacity 180)",
                "Step 6: Select '3. Flight Service' -> Execute POST /api/admin/flights (Schedule AI101 Mumbai -> Delhi)",
                "Step 7: Select '4. Fare Service' -> Execute POST /api/fares (Configure Economy ₹4500, Business ₹8500, Tax 18%)",
                "Step 8: Select '3. Flight Service' -> Execute GET /api/flights/search?source=Mumbai&destination=Delhi",
                "Step 9: Select '5. Booking Service' -> Execute POST /api/bookings (Book seat for Asha Khan, observe PNR & RabbitMQ event)",
                "Step 10: Select '6. Check-In Service' -> Execute POST /api/check-ins (Check in passenger, observe boarding pass BP-1-12A)",
                "Step 11: Demonstrate RBAC Security -> Access GET /api/bookings as regular user (403 Forbidden) vs admin (200 OK)"
        ));
        return ResponseEntity.ok(demo);
    }
}
