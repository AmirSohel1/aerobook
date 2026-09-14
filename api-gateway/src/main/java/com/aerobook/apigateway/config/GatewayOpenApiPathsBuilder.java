package com.aerobook.apigateway.config;

import java.util.List;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;

/**
 * ============================================================================
 * OpenAPI Path and Schema Model Builder for Aerobook API Gateway
 * ============================================================================
 *
 * Programmatic builder utility that constructs OpenAPI 3 paths, operations,
 * parameters, pre-filled JSON request bodies, response models, and DTO
 * component schemas for all microservices in the Aerobook platform.
 *
 * <p>
 * Key Responsibilities:
 * <ul>
 * <li><b>Modular Path Construction:</b> Builds isolated {@link Paths}
 * collections for each microservice (Gateway, Auth, User, Flight, Fare,
 * Booking, Check-In).</li>
 * <li><b>Per-Service OpenAPI Generation:</b> Powers the Swagger UI "Select a
 * definition" dropdown via {@link #buildServiceOpenAPI(String)}, presenting
 * focused documentation to completely eliminate vertical scrolling.</li>
 * <li><b>Complete Platform Aggregation:</b> Merges all paths and tags via
 * {@link #buildAllPaths()} for the complete platform view.</li>
 * <li><b>Comprehensive Schema Registry:</b> Registers 17 complete DTO schemas
 * in {@link Components} with field data types, constraints, and realistic
 * examples.</li>
 * </ul>
 *
 * @author Aerobook Platform Engineering
 * @version 1.0.0
 */
public class GatewayOpenApiPathsBuilder {

    /**
     * Security scheme name used for JWT Bearer token authentication.
     */
    private static final String SECURITY_SCHEME_NAME = "BearerAuth";

    /**
     * Returns the ordered list of primary Swagger tags for grouping operations
     * across all microservices in the complete platform view.
     *
     * @return list of configured {@link Tag} objects
     */
    public static List<Tag> getPlatformTags() {
        return List.of(
                new Tag().name("0. API Gateway Platform Hub").description("Central reverse proxy diagnostics, master endpoint catalog, and live presentation demo"),
                new Tag().name("1. Authentication Service (Port 8082)").description("User registrations, BCrypt password hashing, JWT token lifecycle, and role management"),
                new Tag().name("2. User Profile Service (Port 8084)").description("Customer account profiles, demographics, and administrative user management"),
                new Tag().name("3. Flight Scheduling Service (Port 8087)").description("Flight schedule search, commercial flight scheduling, and aircraft fleet inventory"),
                new Tag().name("4. Fare & Pricing Service (Port 8089)").description("Cabin pricing tiers (Economy, Business, First Class), taxes, and promotional discounts"),
                new Tag().name("5. Booking & Messaging Service (Port 8088)").description("Flight ticket reservations, PNR generation, date & seat validations, RabbitMQ events"),
                new Tag().name("6. Airport Check-In Service (Port 8090)").description("Passenger airport check-in, boarding pass generation (BP-1-12A), and seat confirmation")
        );
    }

    /**
     * Constructs OpenAPI path definitions for the API Gateway Platform Hub
     * (Port 8083). Includes health check, microservice directory, endpoint
     * catalog, RBAC matrix, and demo script.
     *
     * @return {@link Paths} containing all Gateway diagnostic endpoints
     */
    public static Paths buildGatewayPaths() {
        Paths paths = new Paths();
        String tag = "0. API Gateway Platform Hub";

        paths.addPathItem("/api/gateway/health", new PathItem().get(
                createOp(tag, "API Gateway Health & Liveness Probe",
                        "Verifies operational status, port, and reactive routing engine of the central Spring Cloud Gateway.",
                        null,
                        createResponses("200", "API Gateway is operational"))
        ));

        paths.addPathItem("/api/gateway/services", new PathItem().get(
                createOp(tag, "Microservices Architecture & Topology Directory",
                        "Returns complete architectural mapping of all 6 microservices, their ports, database instances, and key responsibilities.",
                        null,
                        createResponses("200", "Microservice directory retrieved successfully"))
        ));

        paths.addPathItem("/api/gateway/endpoints", new PathItem().get(
                createOp(tag, "Master Platform API Catalog",
                        "Comprehensive catalog listing all API endpoints across all microservices, their HTTP methods, required roles, and descriptions.",
                        null,
                        createResponses("200", "Master API endpoint catalog retrieved successfully"))
        ));

        paths.addPathItem("/api/gateway/rbac-matrix", new PathItem().get(
                createOp(tag, "Platform RBAC Security Policy Matrix",
                        "Returns complete Role-Based Access Control security policy mapping for Public, User, and Admin authorities.",
                        null,
                        createResponses("200", "RBAC Matrix retrieved successfully"))
        ));

        paths.addPathItem("/api/gateway/presentation-demo", new PathItem().get(
                createOp(tag, "Teacher Presentation Demo Script",
                        "Provides a structured, step-by-step presentation script for demonstrating the Aerobook distributed platform during evaluation.",
                        null,
                        createResponses("200", "Presentation script retrieved successfully"))
        ));

        return paths;
    }

    /**
     * Constructs OpenAPI path definitions for the Auth Service (Port 8082).
     * Includes registration, admin setup, login, token refresh, credential
     * audit, and role management.
     *
     * @return {@link Paths} containing all Auth Service endpoints
     */
    public static Paths buildAuthPaths() {
        Paths paths = new Paths();
        String tag = "1. Authentication Service (Port 8082)";

        paths.addPathItem("/api/auth/register", new PathItem().post(
                createOp(tag, "Register customer account",
                        "Registers a new customer account with ROLE_USER, syncs profile with user-service, and returns JWT access and refresh tokens.",
                        createJsonBody("""
                                {
                                  "firstName": "Asha",
                                  "lastName": "Khan",
                                  "email": "asha.khan@example.com",
                                  "phoneNumber": "9876543210",
                                  "dateOfBirth": "1998-05-12",
                                  "nationality": "Indian",
                                  "password": "Asha@12345"
                                }
                                """),
                        createResponses("201", "Customer account registered successfully", "400", "Validation error", "409", "Email already exists"))
        ));

        paths.addPathItem("/api/auth/register-admin", new PathItem().post(
                createOp(tag, "Register or promote administrator account",
                        "Registers a new administrator or promotes an existing account to ROLE_ADMIN. Requires secret setup key header.",
                        createJsonBody("""
                                {
                                  "firstName": "Aerobook",
                                  "lastName": "Admin",
                                  "email": "admin@aerobook.com",
                                  "phoneNumber": "9876543200",
                                  "dateOfBirth": "1990-01-01",
                                  "nationality": "Indian",
                                  "password": "Admin@12345"
                                }
                                """),
                        createResponses("201", "Administrator registered successfully", "403", "Invalid setup key", "400", "Validation error"))
                        .addParametersItem(new Parameter().name("X-Admin-Setup-Key").in("header").required(true).example("AerobookAdminSetup2026").description("Secret setup key for admin elevation"))
        ));

        paths.addPathItem("/api/auth/login", new PathItem().post(
                createOp(tag, "Authenticate user and get JWT tokens",
                        "Verifies email and password credentials, returning signed JWT access token and refresh token.",
                        createJsonBody("""
                                {
                                  "email": "asha.khan@example.com",
                                  "password": "Asha@12345"
                                }
                                """),
                        createResponses("200", "Authentication successful, tokens issued", "401", "Invalid credentials", "400", "Missing credentials"))
        ));

        paths.addPathItem("/api/auth/refresh-token", new PathItem().post(
                createOp(tag, "Refresh expired access token",
                        "Generates a new JWT access token using a valid unexpired refresh token.",
                        createJsonBody("""
                                {
                                  "refreshToken": "d9a3b64c-8f4b-4890-a241-119c67bc2f20"
                                }
                                """),
                        createResponses("200", "Access token refreshed", "401", "Invalid or expired refresh token"))
        ));

        paths.addPathItem("/api/auth/credentials", new PathItem().get(
                createOp(tag, "List all platform credentials (ROLE_ADMIN)",
                        "Administrative oversight endpoint to view all registered credentials and roles. Requires ROLE_ADMIN authority.",
                        null,
                        createResponses("200", "Credentials retrieved successfully", "401", "Unauthorized", "403", "Forbidden: Requires ROLE_ADMIN"))
        ));

        paths.addPathItem("/api/auth/credentials/{userId}/role", new PathItem().put(
                createOp(tag, "Update user security role (ROLE_ADMIN)",
                        "Administrative endpoint to promote or demote user between ROLE_USER and ROLE_ADMIN. Requires ROLE_ADMIN authority.",
                        null,
                        createResponses("200", "User role updated successfully", "400", "Invalid role", "403", "Forbidden: Requires ROLE_ADMIN", "404", "User not found"))
                        .addParametersItem(new Parameter().name("userId").in("path").required(true).example("1").description("User database identifier"))
                        .addParametersItem(new Parameter().name("role").in("query").required(true).example("ROLE_ADMIN").description("Target security role (ROLE_USER or ROLE_ADMIN)"))
        ));

        paths.addPathItem("/api/auth/health", new PathItem().get(
                createOp(tag, "Auth Service Health Probe", "Liveness probe for Auth Service.", null, createResponses("200", "Service is operational"))
        ));

        return paths;
    }

    /**
     * Constructs OpenAPI path definitions for the User Profile Service (Port
     * 8084). Includes health probe, profile retrieval, profile updates, account
     * creation, and admin deletion.
     *
     * @return {@link Paths} containing all User Service endpoints
     */
    public static Paths buildUserPaths() {
        Paths paths = new Paths();
        String tag = "2. User Profile Service (Port 8084)";

        paths.addPathItem("/api/v1/users/health", new PathItem().get(
                createOp(tag, "User Service Health Probe", "Liveness probe for User Service.", null, createResponses("200", "Service is operational"))
        ));

        paths.addPathItem("/api/v1/users", new PathItem()
                .get(createOp(tag, "List all registered users (ROLE_ADMIN)",
                        "Returns full list of all user profiles in database. Strictly restricted to ROLE_ADMIN.",
                        null,
                        createResponses("200", "Users retrieved successfully", "401", "Unauthorized", "403", "Forbidden: Requires ROLE_ADMIN")))
                .post(createOp(tag, "Create user profile directly (ROLE_ADMIN)",
                        "Creates a user profile record directly. In normal flow, registration occurs via Auth Service.",
                        createJsonBody("""
                                {
                                  "firstName": "Ravi",
                                  "lastName": "Patel",
                                  "email": "ravi.patel@example.com",
                                  "phoneNumber": "9876543212",
                                  "dateOfBirth": "1995-09-20",
                                  "nationality": "Indian",
                                  "role": "ROLE_USER"
                                }
                                """),
                        createResponses("201", "User profile created", "400", "Validation error", "403", "Forbidden: Requires ROLE_ADMIN")))
        );

        paths.addPathItem("/api/v1/users/{id}", new PathItem()
                .get(createOp(tag, "Get user profile by ID",
                        "Fetches detailed account information for a specific user ID.",
                        null,
                        createResponses("200", "User profile found", "404", "User not found"))
                        .addParametersItem(new Parameter().name("id").in("path").required(true).example("1").description("User database ID")))
                .put(createOp(tag, "Update personal profile details",
                        "Updates personal details of an existing user account.",
                        createJsonBody("""
                                {
                                  "firstName": "Ravi",
                                  "lastName": "Sharma",
                                  "email": "ravi.sharma@example.com",
                                  "phoneNumber": "9876543212",
                                  "dateOfBirth": "1995-09-20",
                                  "nationality": "Indian",
                                  "role": "ROLE_USER"
                                }
                                """),
                        createResponses("200", "Profile updated successfully", "400", "Validation error", "404", "User not found"))
                        .addParametersItem(new Parameter().name("id").in("path").required(true).example("1").description("User database ID")))
                .delete(createOp(tag, "Delete user account (ROLE_ADMIN)",
                        "Permanently removes user account from database. Restricted to ROLE_ADMIN.",
                        null,
                        createResponses("200", "User deleted successfully", "403", "Forbidden: Requires ROLE_ADMIN", "404", "User not found"))
                        .addParametersItem(new Parameter().name("id").in("path").required(true).example("1").description("User database ID")))
        );

        paths.addPathItem("/api/v1/users/email/{email}", new PathItem().get(
                createOp(tag, "Lookup user profile by email",
                        "Finds user profile details by registered email address.",
                        null,
                        createResponses("200", "User profile found", "404", "User not found"))
                        .addParametersItem(new Parameter().name("email").in("path").required(true).example("asha.khan@example.com").description("User email address"))
        ));

        return paths;
    }

    /**
     * Constructs OpenAPI path definitions for the Flight Scheduling Service
     * (Port 8087). Includes route search, full catalog, aircraft fleet
     * management, and commercial flight scheduling.
     *
     * @return {@link Paths} containing all Flight Service endpoints
     */
    public static Paths buildFlightPaths() {
        Paths paths = new Paths();
        String tag = "3. Flight Scheduling Service (Port 8087)";

        paths.addPathItem("/api/flights/health", new PathItem().get(
                createOp(tag, "Flight Service Health Probe", "Liveness probe for Flight Service.", null, createResponses("200", "Service is operational"))
        ));

        paths.addPathItem("/api/flights/search", new PathItem().get(
                createOp(tag, "Search flights by route",
                        "Searches active scheduled flights between source and destination cities.",
                        null,
                        createResponses("200", "Search results retrieved"))
                        .addParametersItem(new Parameter().name("source").in("query").required(true).example("Mumbai").description("Departure city / airport"))
                        .addParametersItem(new Parameter().name("destination").in("query").required(true).example("Delhi").description("Arrival city / airport"))
        ));

        paths.addPathItem("/api/flights", new PathItem().get(
                createOp(tag, "Browse complete scheduled flights catalog",
                        "Returns all scheduled commercial flights in the platform.",
                        null,
                        createResponses("200", "Catalog retrieved successfully"))
        ));

        paths.addPathItem("/api/flights/{id}", new PathItem().get(
                createOp(tag, "Get flight details and available seats",
                        "Retrieves flight timetable, seat availability, and base fare by flight ID.",
                        null,
                        createResponses("200", "Flight details found", "404", "Flight not found"))
                        .addParametersItem(new Parameter().name("id").in("path").required(true).example("1").description("Flight database identifier"))
        ));

        paths.addPathItem("/api/admin/aircrafts", new PathItem()
                .post(createOp(tag, "Register new aircraft model (ROLE_ADMIN)",
                        "Registers a new commercial aircraft model into the airline fleet. Restricted to ROLE_ADMIN.",
                        createJsonBody("""
                                {
                                  "model": "Airbus A320",
                                  "registrationNumber": "A320-001",
                                  "capacity": 180
                                }
                                """),
                        createResponses("201", "Aircraft registered successfully", "403", "Forbidden: Requires ROLE_ADMIN")))
                .get(createOp(tag, "List all aircraft in fleet (ROLE_ADMIN)",
                        "Returns inventory of all registered fleet aircraft and seating capacities. Restricted to ROLE_ADMIN.",
                        null,
                        createResponses("200", "Fleet inventory retrieved", "403", "Forbidden: Requires ROLE_ADMIN")))
        );

        paths.addPathItem("/api/admin/aircrafts/{id}", new PathItem()
                .get(createOp(tag, "Inspect fleet aircraft by ID (ROLE_ADMIN)",
                        "Retrieves fleet aircraft record by primary ID. Restricted to ROLE_ADMIN.",
                        null,
                        createResponses("200", "Aircraft found", "403", "Forbidden", "404", "Aircraft not found"))
                        .addParametersItem(new Parameter().name("id").in("path").required(true).example("1").description("Aircraft ID")))
                .delete(createOp(tag, "Decommission aircraft from fleet (ROLE_ADMIN)",
                        "Permanently removes an aircraft from fleet inventory. Restricted to ROLE_ADMIN.",
                        null,
                        createResponses("200", "Aircraft deleted successfully", "403", "Forbidden", "404", "Aircraft not found"))
                        .addParametersItem(new Parameter().name("id").in("path").required(true).example("1").description("Aircraft ID")))
        );

        paths.addPathItem("/api/admin/flights", new PathItem()
                .post(createOp(tag, "Schedule new commercial flight (ROLE_ADMIN)",
                        "Schedules a commercial flight, links to fleet aircraft, and sets departure/arrival times. Restricted to ROLE_ADMIN.",
                        createJsonBody("""
                                {
                                  "flightNumber": "AI101",
                                  "airlineName": "Air India",
                                  "source": "Mumbai",
                                  "destination": "Delhi",
                                  "departureTime": "2026-10-01T06:00:00",
                                  "arrivalTime": "2026-10-01T08:15:00",
                                  "aircraftId": 1,
                                  "baseFare": 4500.00
                                }
                                """),
                        createResponses("201", "Flight scheduled successfully", "403", "Forbidden: Requires ROLE_ADMIN")))
                .get(createOp(tag, "Administrative oversight of all flights (ROLE_ADMIN)",
                        "Administrative view of all flight schedules across the airline. Restricted to ROLE_ADMIN.",
                        null,
                        createResponses("200", "Flight schedules retrieved", "403", "Forbidden: Requires ROLE_ADMIN")))
        );

        paths.addPathItem("/api/admin/flights/{id}", new PathItem()
                .put(createOp(tag, "Reschedule or update flight (ROLE_ADMIN)",
                        "Updates flight timings, route, or aircraft allocation. Restricted to ROLE_ADMIN.",
                        createJsonBody("""
                                {
                                  "flightNumber": "AI101",
                                  "airlineName": "Air India",
                                  "source": "Mumbai",
                                  "destination": "Delhi",
                                  "departureTime": "2026-10-01T07:00:00",
                                  "arrivalTime": "2026-10-01T09:15:00",
                                  "aircraftId": 1,
                                  "baseFare": 4800.00
                                }
                                """),
                        createResponses("200", "Flight updated successfully", "403", "Forbidden", "404", "Flight not found"))
                        .addParametersItem(new Parameter().name("id").in("path").required(true).example("1").description("Flight ID")))
                .delete(createOp(tag, "Cancel commercial flight (ROLE_ADMIN)",
                        "Cancels or deletes a flight schedule. Restricted to ROLE_ADMIN.",
                        null,
                        createResponses("200", "Flight cancelled successfully", "403", "Forbidden", "404", "Flight not found"))
                        .addParametersItem(new Parameter().name("id").in("path").required(true).example("1").description("Flight ID")))
        );

        return paths;
    }

    /**
     * Constructs OpenAPI path definitions for the Fare & Pricing Service (Port
     * 8089). Includes health probe, fare listings, fare lookup by ID and Flight
     * ID, and tiered pricing management.
     *
     * @return {@link Paths} containing all Fare Service endpoints
     */
    public static Paths buildFarePaths() {
        Paths paths = new Paths();
        String tag = "4. Fare & Pricing Service (Port 8089)";

        paths.addPathItem("/api/fares/health", new PathItem().get(
                createOp(tag, "Fare Service Health Probe", "Liveness probe for Fare Service.", null, createResponses("200", "Service is operational"))
        ));

        paths.addPathItem("/api/fares", new PathItem()
                .get(createOp(tag, "List all configured fares",
                        "Returns full list of all flight fares in the system.",
                        null,
                        createResponses("200", "List of fares retrieved")))
                .post(createOp(tag, "Configure tiered pricing for flight (ROLE_ADMIN)",
                        "Configures cabin pricing tiers (Economy, Business, First Class), taxes, and discounts for a flight. Restricted to ROLE_ADMIN.",
                        createJsonBody("""
                                {
                                  "flightId": 1,
                                  "economyFare": 4500.00,
                                  "businessFare": 8500.00,
                                  "firstClassFare": 14000.00,
                                  "taxPercentage": 18.0,
                                  "discountPercentage": 5.0,
                                  "effectiveDate": "2026-10-01"
                                }
                                """),
                        createResponses("201", "Fare created successfully", "400", "Invalid fare payload", "403", "Forbidden: Requires ROLE_ADMIN")))
        );

        paths.addPathItem("/api/fares/{id}", new PathItem()
                .get(createOp(tag, "Get fare by ID",
                        "Retrieves pricing breakdown for a specific fare ID.",
                        null,
                        createResponses("200", "Fare details retrieved", "404", "Fare not found"))
                        .addParametersItem(new Parameter().name("id").in("path").required(true).example("1").description("Fare database ID")))
                .put(createOp(tag, "Update fare pricing (ROLE_ADMIN)",
                        "Updates prices, discounts, or tax percentages for an existing fare ID. Restricted to ROLE_ADMIN.",
                        createJsonBody("""
                                {
                                  "flightId": 1,
                                  "economyFare": 4800.00,
                                  "businessFare": 9000.00,
                                  "firstClassFare": 15000.00,
                                  "taxPercentage": 18.0,
                                  "discountPercentage": 10.0,
                                  "effectiveDate": "2026-10-01"
                                }
                                """),
                        createResponses("200", "Fare updated successfully", "400", "Invalid fare payload", "403", "Forbidden", "404", "Fare not found"))
                        .addParametersItem(new Parameter().name("id").in("path").required(true).example("1").description("Fare database ID")))
                .delete(createOp(tag, "Delete fare record (ROLE_ADMIN)",
                        "Permanently removes a fare record by ID. Restricted to ROLE_ADMIN.",
                        null,
                        createResponses("200", "Fare deleted successfully", "403", "Forbidden", "404", "Fare not found"))
                        .addParametersItem(new Parameter().name("id").in("path").required(true).example("1").description("Fare database ID")))
        );

        paths.addPathItem("/api/fares/flight/{flightId}", new PathItem().get(
                createOp(tag, "Get fare by Flight ID",
                        "Retrieves active fare configuration for a specific scheduled flight ID.",
                        null,
                        createResponses("200", "Fare details for flight", "404", "No fare found for flight"))
                        .addParametersItem(new Parameter().name("flightId").in("path").required(true).example("1").description("Flight database ID"))
        ));

        return paths;
    }

    /**
     * Constructs OpenAPI path definitions for the Booking & Messaging Service
     * (Port 8088). Includes health probe, reservation creation with PNR,
     * booking lookup, user bookings, and cancellation.
     *
     * @return {@link Paths} containing all Booking Service endpoints
     */
    public static Paths buildBookingPaths() {
        Paths paths = new Paths();
        String tag = "5. Booking & Messaging Service (Port 8088)";

        paths.addPathItem("/api/bookings/health", new PathItem().get(
                createOp(tag, "Booking Service Health Probe", "Liveness probe for Booking Service.", null, createResponses("200", "Service is operational"))
        ));

        paths.addPathItem("/api/bookings", new PathItem()
                .post(createOp(tag, "Create flight booking",
                        "Validates flight existence, departure date (rejects past flights), and seat capacity via OpenFeign. "
                        + "Computes fare, assigns PNR, saves booking, and publishes RabbitMQ event.",
                        createJsonBody("""
                                {
                                  "userId": 1,
                                  "flightId": 1,
                                  "passengers": [
                                    {
                                      "firstName": "Asha",
                                      "lastName": "Khan",
                                      "age": 29,
                                      "gender": "F"
                                    }
                                  ]
                                }
                                """),
                        createResponses("200", "Booking confirmed successfully", "400", "Past departure / insufficient seats", "404", "Flight not found")))
                .get(createOp(tag, "List all bookings across airline (ROLE_ADMIN)",
                        "Administrative oversight endpoint to view all passenger bookings in the system. Strictly restricted to ROLE_ADMIN.",
                        null,
                        createResponses("200", "Bookings retrieved successfully", "401", "Unauthorized", "403", "Forbidden: Requires ROLE_ADMIN")))
        );

        paths.addPathItem("/api/bookings/{bookingId}", new PathItem()
                .get(createOp(tag, "Lookup booking by Database ID",
                        "Retrieves reservation details, passenger manifest, and flight itinerary by primary booking ID.",
                        null,
                        createResponses("200", "Booking found", "404", "Booking not found"))
                        .addParametersItem(new Parameter().name("bookingId").in("path").required(true).example("1").description("Booking primary database ID")))
                .delete(createOp(tag, "Cancel flight booking (safeguarded)",
                        "Cancels a confirmed reservation by booking ID. Safeguards against double cancellation.",
                        null,
                        createResponses("200", "Booking cancelled successfully", "400", "Already cancelled", "404", "Booking not found"))
                        .addParametersItem(new Parameter().name("bookingId").in("path").required(true).example("1").description("Booking database ID to cancel")))
        );

        paths.addPathItem("/api/bookings/pnr/{pnr}", new PathItem().get(
                createOp(tag, "Lookup booking by PNR",
                        "Retrieves reservation details, flight information, and passenger manifest using Passenger Name Record (PNR).",
                        null,
                        createResponses("200", "Booking found", "404", "Booking not found with specified PNR"))
                        .addParametersItem(new Parameter().name("pnr").in("path").required(true).example("AB1001").description("Passenger Name Record code"))
        ));

        paths.addPathItem("/api/bookings/user/{userId}", new PathItem().get(
                createOp(tag, "Get bookings by user account ID",
                        "Retrieves all flight reservations booked under a specific user account ID.",
                        null,
                        createResponses("200", "Bookings retrieved successfully"))
                        .addParametersItem(new Parameter().name("userId").in("path").required(true).example("1").description("User account ID"))
        ));

        return paths;
    }

    /**
     * Constructs OpenAPI path definitions for the Airport Check-In Service
     * (Port 8090). Includes health probe, check-in completion, boarding pass
     * issuance (BP-1-12A), and admin audit.
     *
     * @return {@link Paths} containing all Check-In Service endpoints
     */
    public static Paths buildCheckInPaths() {
        Paths paths = new Paths();
        String tag = "6. Airport Check-In Service (Port 8090)";

        paths.addPathItem("/api/check-ins/health", new PathItem().get(
                createOp(tag, "Check-In Service Health Probe", "Liveness probe for Check-In Service.", null, createResponses("200", "Service is operational"))
        ));

        paths.addPathItem("/api/check-ins", new PathItem()
                .post(createOp(tag, "Check in passenger & generate boarding pass",
                        "Performs check-in for a passenger on a confirmed booking, assigns seat, generates boarding pass (BP-1-12A), and marks CHECKED_IN.",
                        createJsonBody("""
                                {
                                  "bookingId": 1,
                                  "passengerName": "Asha Khan",
                                  "seatNumber": "12A"
                                }
                                """),
                        createResponses("200", "Check-in successful, boarding pass issued", "400", "Invalid parameters")))
                .get(createOp(tag, "List all check-ins across airline (ROLE_ADMIN)",
                        "Administrative audit endpoint returning all passenger check-in confirmations. Strictly restricted to ROLE_ADMIN.",
                        null,
                        createResponses("200", "List of all check-in records", "401", "Unauthorized", "403", "Forbidden: Requires ROLE_ADMIN")))
        );

        paths.addPathItem("/api/check-ins/booking/{bookingId}", new PathItem().get(
                createOp(tag, "Get check-in record by booking ID",
                        "Retrieves existing check-in details, assigned seat, and boarding pass for a booking ID.",
                        null,
                        createResponses("200", "Check-in record found", "404", "Check-in not found for booking"))
                        .addParametersItem(new Parameter().name("bookingId").in("path").required(true).example("1").description("Booking database ID"))
        ));

        paths.addPathItem("/api/check-ins/{id}", new PathItem().get(
                createOp(tag, "Get check-in record by ID",
                        "Retrieves existing check-in confirmation by primary check-in ID.",
                        null,
                        createResponses("200", "Check-in record found", "404", "Check-in not found"))
                        .addParametersItem(new Parameter().name("id").in("path").required(true).example("1").description("Check-in primary ID"))
        ));

        return paths;
    }

    /**
     * Aggregates all paths across all 7 microservices into a single Paths
     * collection. Used by the complete platform view.
     *
     * @return combined {@link Paths} containing all endpoints across the
     * platform
     */
    public static Paths buildAllPaths() {
        Paths paths = new Paths();
        buildGatewayPaths().forEach(paths::addPathItem);
        buildAuthPaths().forEach(paths::addPathItem);
        buildUserPaths().forEach(paths::addPathItem);
        buildFlightPaths().forEach(paths::addPathItem);
        buildFarePaths().forEach(paths::addPathItem);
        buildBookingPaths().forEach(paths::addPathItem);
        buildCheckInPaths().forEach(paths::addPathItem);
        return paths;
    }

    /**
     * Builds an isolated, self-contained OpenAPI 3 specification for a selected
     * microservice. Powers the Swagger UI definition dropdown, enabling focused
     * inspection without vertical scrolling.
     *
     * @param serviceId case-insensitive service key (e.g. "auth-service",
     * "flight-service", "gateway")
     * @return {@link OpenAPI} specification containing strictly the selected
     * service's endpoints
     */
    public static OpenAPI buildServiceOpenAPI(String serviceId) {
        if (serviceId == null) {
            return buildAllOpenAPI();
        }
        String key = serviceId.toLowerCase().trim();
        return switch (key) {
            case "auth", "auth-service" ->
                buildIndividualOpenAPI(
                "🔑 Aerobook - Authentication & Security Authority Service (Port 8082)",
                """
                    # 🔑 Authentication & Security Authority Service (Port 8082)
                    
                    Dedicated microservice specification for **Auth Service**.
                    
                    ### 🏛️ Service Specifications
                    - **Service Port**: `8082`
                    - **Database**: `aerobook_auth_db` (MySQL)
                    - **Primary Responsibility**: User registration, password encryption (BCrypt), JWT token issuance (Access & Refresh), role management, master admin elevation.
                    - **Gateway Routing Prefix**: `/api/auth/**`
                    
                    ### 🔐 Security & Roles
                    - `POST /api/auth/register` & `POST /api/auth/login`: Public
                    - `POST /api/auth/register-admin`: Guarded by `X-Admin-Setup-Key: AerobookAdminSetup2026`
                    - `GET /api/auth/credentials` & `PUT /api/auth/credentials/{userId}/role`: Restricted to `ROLE_ADMIN`
                    """,
                "1. Authentication Service (Port 8082)",
                "User registrations, BCrypt password hashing, JWT token lifecycle, and role management",
                buildAuthPaths()
                );
            case "user", "user-service" ->
                buildIndividualOpenAPI(
                "👤 Aerobook - User Profile Management Service (Port 8084)",
                """
                    # 👤 User Profile Management Service (Port 8084)
                    
                    Dedicated microservice specification for **User Profile Service**.
                    
                    ### 🏛️ Service Specifications
                    - **Service Port**: `8084`
                    - **Database**: `aerobook_user_db` (MySQL)
                    - **Primary Responsibility**: Customer accounts, personal profile management, birth date, contact info, demographics, and administrative user lists.
                    - **Gateway Routing Prefix**: `/api/v1/users/**`
                    
                    ### 🔐 Security & Roles
                    - `GET/PUT /api/v1/users/{id}`, `GET /api/v1/users/email/{email}`: `ROLE_USER`, `ROLE_ADMIN`
                    - `GET /api/v1/users`, `POST /api/v1/users`, `DELETE /api/v1/users/{id}`: Restricted to `ROLE_ADMIN`
                    """,
                "2. User Profile Service (Port 8084)",
                "Customer account profiles, demographics, and administrative user management",
                buildUserPaths()
                );
            case "flight", "flight-service" ->
                buildIndividualOpenAPI(
                "✈️ Aerobook - Flight Scheduling & Fleet Management Service (Port 8087)",
                """
                    # ✈️ Flight Scheduling & Fleet Management Service (Port 8087)
                    
                    Dedicated microservice specification for **Flight Service**.
                    
                    ### 🏛️ Service Specifications
                    - **Service Port**: `8087`
                    - **Database**: `aerobook_flight_db` (MySQL)
                    - **Primary Responsibility**: Flight timetable search (Source $\\rightarrow$ Destination), commercial flight scheduling, fleet aircraft inventory (Airbus, Boeing).
                    - **Gateway Routing Prefix**: `/api/flights/**`, `/api/admin/aircrafts/**`, `/api/admin/flights/**`
                    
                    ### 🔐 Security & Roles
                    - `GET /api/flights/search`, `GET /api/flights`, `GET /api/flights/{id}`: `ROLE_USER`, `ROLE_ADMIN`
                    - `/api/admin/aircrafts/**`, `/api/admin/flights/**`: Restricted to `ROLE_ADMIN`
                    """,
                "3. Flight Scheduling Service (Port 8087)",
                "Flight schedule search, commercial flight scheduling, and aircraft fleet inventory",
                buildFlightPaths()
                );
            case "fare", "fare-service" ->
                buildIndividualOpenAPI(
                "💳 Aerobook - Fare & Revenue Management Service (Port 8089)",
                """
                    # 💳 Fare & Revenue Management Service (Port 8089)
                    
                    Dedicated microservice specification for **Fare Service**.
                    
                    ### 🏛️ Service Specifications
                    - **Service Port**: `8089`
                    - **Database**: `aerobook_fare_db` (MySQL)
                    - **Primary Responsibility**: Tiered cabin pricing (Economy, Business, First Class), GST tax calculations, promotional discounts, and pricing updates.
                    - **Gateway Routing Prefix**: `/api/fares/**`
                    
                    ### 🔐 Security & Roles
                    - `GET /api/fares/**`: `ROLE_USER`, `ROLE_ADMIN`
                    - `POST/PUT/DELETE /api/fares/**`: Restricted to `ROLE_ADMIN`
                    """,
                "4. Fare & Pricing Service (Port 8089)",
                "Cabin pricing tiers (Economy, Business, First Class), taxes, and promotional discounts",
                buildFarePaths()
                );
            case "booking", "booking-service" ->
                buildIndividualOpenAPI(
                "🎟️ Aerobook - Booking & Messaging Service (Port 8088)",
                """
                    # 🎟️ Booking & Messaging Service (Port 8088)
                    
                    Dedicated microservice specification for **Booking Service**.
                    
                    ### 🏛️ Service Specifications
                    - **Service Port**: `8088`
                    - **Database**: `aerobook_booking_db` (MySQL)
                    - **Primary Responsibility**: Flight reservations, PNR generation, OpenFeign flight capacity checks, past departure date safeguards, RabbitMQ `BOOKING_CREATED` asynchronous event publishing.
                    - **Gateway Routing Prefix**: `/api/bookings/**`
                    
                    ### 🔐 Security & Roles
                    - `POST /api/bookings`, `GET /api/bookings/{id}`, `GET /api/bookings/pnr/{pnr}`, `DELETE /api/bookings/{id}`: `ROLE_USER`, `ROLE_ADMIN`
                    - `GET /api/bookings` (All bookings): Restricted to `ROLE_ADMIN`
                    """,
                "5. Booking & Messaging Service (Port 8088)",
                "Flight ticket reservations, PNR generation, date & seat validations, RabbitMQ events",
                buildBookingPaths()
                );
            case "check-in", "checkin", "check-in-service" ->
                buildIndividualOpenAPI(
                "🛂 Aerobook - Airport Check-In & Boarding Pass Service (Port 8090)",
                """
                    # 🛂 Airport Check-In & Boarding Pass Service (Port 8090)
                    
                    Dedicated microservice specification for **Check-In Service**.
                    
                    ### 🏛️ Service Specifications
                    - **Service Port**: `8090`
                    - **Database**: `aerobook_checkin_db` (MySQL)
                    - **Primary Responsibility**: Passenger airport check-in, seat assignment, barcode boarding pass generation (`BP-1-12A`), confirmation timestamps, and status updates.
                    - **Gateway Routing Prefix**: `/api/check-ins/**`
                    
                    ### 🔐 Security & Roles
                    - `POST /api/check-ins`, `GET /api/check-ins/booking/{id}`, `GET /api/check-ins/{id}`: `ROLE_USER`, `ROLE_ADMIN`
                    - `GET /api/check-ins` (All check-ins audit): Restricted to `ROLE_ADMIN`
                    """,
                "6. Airport Check-In Service (Port 8090)",
                "Passenger airport check-in, boarding pass generation (BP-1-12A), and seat confirmation",
                buildCheckInPaths()
                );
            case "gateway", "gateway-service", "gateway-hub" ->
                buildIndividualOpenAPI(
                "🌐 Aerobook - API Gateway Platform Hub (Port 8083)",
                """
                    # 🌐 API Gateway Platform Hub (Port 8083)
                    
                    Central diagnostics, microservice topology directory, master platform API catalog, RBAC matrix, and teacher presentation demo script.
                    
                    ### 🏛️ Service Specifications
                    - **Service Port**: `8083`
                    - **Routing Engine**: Spring Cloud Gateway Reactive WebFlux
                    - **Gateway Routing Prefix**: `/api/gateway/**`
                    """,
                "0. API Gateway Platform Hub",
                "Central reverse proxy diagnostics, master endpoint catalog, and live presentation demo",
                buildGatewayPaths()
                );
            default ->
                buildAllOpenAPI();
        };
    }

    /**
     * Helper to assemble an isolated OpenAPI instance for a specific
     * microservice.
     *
     * @param title service-specific document title
     * @param description markdown documentation text
     * @param tagName primary tag category name
     * @param tagDesc description of tag category
     * @param paths paths specific to the service
     * @return constructed {@link OpenAPI} object
     */
    private static OpenAPI buildIndividualOpenAPI(String title, String description, String tagName, String tagDesc, Paths paths) {
        return new OpenAPI()
                .info(new Info()
                        .title(title)
                        .version("1.0.0 (Enterprise Business Edition)")
                        .description(description)
                        .contact(new Contact()
                                .name("Aerobook Platform Engineering")
                                .email("platform@aerobook.com")
                                .url("https://aerobook.com"))
                        .license(new License()
                                .name("Academic Evaluation License - Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                .servers(List.of(
                        new Server().url("http://localhost:8083").description("Aerobook API Gateway (Centralized Reverse Proxy)")
                ))
                .tags(List.of(new Tag().name(tagName).description(tagDesc)))
                .paths(paths)
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(buildComponents());
    }

    /**
     * Constructs the master platform OpenAPI specification containing all
     * services and tags.
     *
     * @return complete {@link OpenAPI} document
     */
    public static OpenAPI buildAllOpenAPI() {
        return new OpenAPI()
                .tags(getPlatformTags())
                .paths(buildAllPaths())
                .info(new Info()
                        .title("✈️ Aerobook - Distributed Airline Reservation Platform")
                        .version("1.0.0 (Enterprise Business Edition)")
                        .description("""
                                # 🌐 Central API Gateway & Master Platform Documentation
                                
                                Welcome to the **Aerobook Cloud Airline Platform**!
                                The **API Gateway** acts as the single entry point, reverse proxy, JWT security validator, 
                                and centralized Swagger aggregator for all backend microservices.
                                
                                Use the **Select a definition** dropdown above to quickly jump to any individual microservice 
                                documentation and avoid long vertical scrolling!
                                
                                ---
                                
                                ### 🏛️ Microservice Topology & Port Directory
                                
                                | Microservice | Port | Database | Primary Responsibility | Routing Prefix |
                                | :--- | :--- | :--- | :--- | :--- |
                                | **API Gateway** | `8083` | N/A | Reverse Proxy, JWT Validation, Role Auditing, CORS | `/api/**` |
                                | **Auth Service** | `8082` | `aerobook_auth_db` | User Registration, BCrypt Hashing, JWT Lifecycle, Roles | `/api/auth/**` |
                                | **User Service** | `8084` | `aerobook_user_db` | Customer Profiles, Demographics, Administrative CRUD | `/api/v1/users/**` |
                                | **Flight Service** | `8087` | `aerobook_flight_db` | Flight Search Engine, Flight Scheduling, Fleet Inventory | `/api/flights/**`, `/api/admin/**` |
                                | **Fare Service** | `8089` | `aerobook_fare_db` | Cabin Pricing (Economy, Business, First), Taxes, Discounts | `/api/fares/**` |
                                | **Booking Service** | `8088` | `aerobook_booking_db` | Seat Reservations, PNR Engine, Date & Capacity Checks, RabbitMQ | `/api/bookings/**` |
                                | **Check-In Service** | `8090` | `aerobook_checkin_db` | Passenger Airport Check-in, Boarding Pass (`BP-1-12A`) | `/api/check-ins/**` |
                                
                                ---
                                
                                ### 📋 Master Platform API Endpoints Catalog
                                
                                #### 1. Authentication & Security Authority Service (Port 8082)
                                | Method | Endpoint | Allowed Authority | Description |
                                | :--- | :--- | :--- | :--- |
                                | `GET` | `/api/auth/health` | **Public** | Auth Service health & liveness probe |
                                | `POST` | `/api/auth/register` | **Public** | Register customer user account (`ROLE_USER`) |
                                | `POST` | `/api/auth/register-admin` | **`X-Admin-Setup-Key`** | Master administrator setup / role elevation |
                                | `POST` | `/api/auth/login` | **Public** | Authenticate credentials and issue JWT tokens |
                                | `POST` | `/api/auth/refresh-token` | **Public** | Exchange refresh token for new access token |
                                | `GET` | `/api/auth/credentials` | 🔴 **`ROLE_ADMIN` Only** | Audit all platform credentials and security roles |
                                | `PUT` | `/api/auth/credentials/{userId}/role`| 🔴 **`ROLE_ADMIN` Only** | Promote or demote user authority |
                                
                                #### 2. User Profile Management Service (Port 8084)
                                | Method | Endpoint | Allowed Authority | Description |
                                | :--- | :--- | :--- | :--- |
                                | `GET` | `/api/v1/users/health` | **Public** | User Service health probe |
                                | `GET` | `/api/v1/users/{id}` | `ROLE_USER`, `ROLE_ADMIN` | Retrieve user profile by database ID |
                                | `GET` | `/api/v1/users/email/{email}`| `ROLE_USER`, `ROLE_ADMIN` | Lookup user profile by registered email |
                                | `PUT` | `/api/v1/users/{id}` | `ROLE_USER`, `ROLE_ADMIN` | Update personal account details |
                                | `GET` | `/api/v1/users` | 🔴 **`ROLE_ADMIN` Only** | List all registered customer accounts |
                                | `POST` | `/api/v1/users` | 🔴 **`ROLE_ADMIN` Only** | Create user account directly |
                                | `DELETE` | `/api/v1/users/{id}` | 🔴 **`ROLE_ADMIN` Only** | Permanently delete user account |
                                
                                #### 3. Flight Scheduling & Fleet Management Service (Port 8087)
                                | Method | Endpoint | Allowed Authority | Description |
                                | :--- | :--- | :--- | :--- |
                                | `GET` | `/api/flights/health` | **Public** | Flight Service health probe |
                                | `GET` | `/api/flights/search` | `ROLE_USER`, `ROLE_ADMIN` | Search active flights by departure & arrival cities |
                                | `GET` | `/api/flights` | `ROLE_USER`, `ROLE_ADMIN` | Browse full flight catalog |
                                | `GET` | `/api/flights/{id}` | `ROLE_USER`, `ROLE_ADMIN` | View flight details, schedule, and seat capacity |
                                | `POST` | `/api/admin/aircrafts` | 🔴 **`ROLE_ADMIN` Only** | Register new aircraft model into airline fleet |
                                | `GET` | `/api/admin/aircrafts` | 🔴 **`ROLE_ADMIN` Only** | View fleet inventory and capacities |
                                | `GET/DELETE` | `/api/admin/aircrafts/{id}`| 🔴 **`ROLE_ADMIN` Only** | Inspect or decommission fleet aircraft |
                                | `POST` | `/api/admin/flights` | 🔴 **`ROLE_ADMIN` Only** | Schedule a new commercial flight |
                                | `PUT/DELETE` | `/api/admin/flights/{id}` | 🔴 **`ROLE_ADMIN` Only** | Reschedule, modify timings, or cancel flight |
                                | `GET` | `/api/admin/flights` | 🔴 **`ROLE_ADMIN` Only** | Administrative view of all flight schedules |
                                
                                #### 4. Fare & Revenue Management Service (Port 8089)
                                | Method | Endpoint | Allowed Authority | Description |
                                | :--- | :--- | :--- | :--- |
                                | `GET` | `/api/fares/health` | **Public** | Fare Service health probe |
                                | `GET` | `/api/fares/{id}` | `ROLE_USER`, `ROLE_ADMIN` | Retrieve pricing breakdown by Fare ID |
                                | `GET` | `/api/fares/flight/{flightId}` | `ROLE_USER`, `ROLE_ADMIN` | Retrieve active fare pricing for flight |
                                | `GET` | `/api/fares` | `ROLE_USER`, `ROLE_ADMIN` | List all flight fares |
                                | `POST` | `/api/fares` | 🔴 **`ROLE_ADMIN` Only** | Configure tiered pricing for flight |
                                | `PUT` | `/api/fares/{id}` | 🔴 **`ROLE_ADMIN` Only** | Update fare prices, discounts, or taxes |
                                | `DELETE`| `/api/fares/{id}` | 🔴 **`ROLE_ADMIN` Only** | Permanently delete a fare configuration |
                                
                                #### 5. Booking & Messaging Service (Port 8088)
                                | Method | Endpoint | Allowed Authority | Description |
                                | :--- | :--- | :--- | :--- |
                                | `GET` | `/api/bookings/health` | **Public** | Booking Service health probe |
                                | `POST` | `/api/bookings` | `ROLE_USER`, `ROLE_ADMIN` | Reserve flight seats (Date, status, seat validation) |
                                | `GET` | `/api/bookings/{id}` | `ROLE_USER`, `ROLE_ADMIN` | Retrieve reservation details by booking ID |
                                | `GET` | `/api/bookings/pnr/{pnr}` | `ROLE_USER`, `ROLE_ADMIN` | Retrieve reservation details by PNR |
                                | `GET` | `/api/bookings/user/{userId}`| `ROLE_USER`, `ROLE_ADMIN` | List all bookings under user account |
                                | `GET` | `/api/bookings` | 🔴 **`ROLE_ADMIN` Only** | Administrative view of all airline bookings |
                                | `DELETE`| `/api/bookings/{id}` | `ROLE_USER`, `ROLE_ADMIN` | Cancel reservation (safeguarded) |
                                
                                #### 6. Airport Check-In & Boarding Pass Service (Port 8090)
                                | Method | Endpoint | Allowed Authority | Description |
                                | :--- | :--- | :--- | :--- |
                                | `GET` | `/api/check-ins/health` | **Public** | Check-In Service health probe |
                                | `POST` | `/api/check-ins` | `ROLE_USER`, `ROLE_ADMIN` | Check in passenger & issue boarding pass (`BP-1-12A`) |
                                | `GET` | `/api/check-ins/booking/{id}`| `ROLE_USER`, `ROLE_ADMIN` | Retrieve check-in record by booking ID |
                                | `GET` | `/api/check-ins/{id}` | `ROLE_USER`, `ROLE_ADMIN` | Retrieve check-in record by primary ID |
                                | `GET` | `/api/check-ins` | 🔴 **`ROLE_ADMIN` Only** | Administrative view of all passenger check-ins |
                                
                                #### 7. API Gateway Platform Hub (Port 8083)
                                | Method | Endpoint | Allowed Authority | Description |
                                | :--- | :--- | :--- | :--- |
                                | `GET` | `/api/gateway/health` | **Public** | Central API Gateway status & routing engine |
                                | `GET` | `/api/gateway/services` | **Public** | Microservices topology and port directory |
                                | `GET` | `/api/gateway/endpoints` | **Public** | JSON catalog of all platform endpoints |
                                | `GET` | `/api/gateway/rbac-matrix` | **Public** | Structured platform RBAC security model |
                                | `GET` | `/api/gateway/presentation-demo` | **Public** | Structured presentation live demo script |
                                
                                ---
                                
                                ### 🎓 Live Evaluation Walkthrough for Teacher Presentation
                                
                                Use the top-right **Select a definition** dropdown to test all microservices from this central hub:
                                1. **Step 1: Admin Registration**: Select `1. Auth Service` $\\rightarrow$ Execute `POST /api/auth/register-admin`. Copy `accessToken`.
                                2. **Step 2: Authorize Globally**: Click the green **Authorize 🔓** button at top right $\\rightarrow$ Paste token $\\rightarrow$ Click **Authorize**.
                                3. **Step 3: Register Aircraft**: Select `3. Flight Service` $\\rightarrow$ Execute `POST /api/admin/aircrafts` (Airbus A320, 180 seats).
                                4. **Step 4: Schedule Flight**: Select `3. Flight Service` $\\rightarrow$ Execute `POST /api/admin/flights` (Flight `AI101` Mumbai $\\rightarrow$ Delhi).
                                5. **Step 5: Set Fare Pricing**: Select `4. Fare Service` $\\rightarrow$ Execute `POST /api/fares` (Economy ₹4500, Tax 18%).
                                6. **Step 6: Customer Search**: Select `3. Flight Service` $\\rightarrow$ Execute `GET /api/flights/search?source=Mumbai&destination=Delhi`.
                                7. **Step 7: Reserve Ticket**: Select `5. Booking Service` $\\rightarrow$ Execute `POST /api/bookings` for Asha Khan. Note the generated PNR.
                                8. **Step 8: Check-In & Boarding**: Select `6. Check-In Service` $\\rightarrow$ Execute `POST /api/check-ins` for Booking `1` (Seat `12A`).
                                9. **Step 9: RBAC Demonstration**: Attempting admin-restricted endpoints without `ROLE_ADMIN` authority returns `403 Forbidden`.
                                """)
                        .contact(new Contact()
                                .name("Aerobook Platform Engineering")
                                .email("platform@aerobook.com")
                                .url("https://aerobook.com"))
                        .license(new License()
                                .name("Academic Evaluation License - Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                .servers(List.of(
                        new Server().url("http://localhost:8083").description("Aerobook API Gateway (Centralized Reverse Proxy)")
                ))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(buildComponents());
    }

    /**
     * Helper to construct an OpenAPI Operation object with tag, summary,
     * description, body, and responses.
     *
     * @param tag group category tag
     * @param summary short operation title
     * @param description detailed markdown explanation
     * @param body pre-filled request body payload (or null if none)
     * @param responses expected response status codes and descriptions
     * @return populated {@link Operation} object
     */
    private static Operation createOp(String tag, String summary, String description, RequestBody body, ApiResponses responses) {
        Operation op = new Operation();
        op.addTagsItem(tag);
        op.setSummary(summary);
        op.setDescription(description);
        if (body != null) {
            op.setRequestBody(body);
        }
        op.setResponses(responses);
        return op;
    }

    /**
     * Constructs a RequestBody containing a pre-formatted JSON example string.
     *
     * @param jsonExample realistic JSON sample string
     * @return configured {@link RequestBody} object
     */
    private static RequestBody createJsonBody(String jsonExample) {
        RequestBody body = new RequestBody();
        Content content = new Content();
        MediaType mediaType = new MediaType();
        mediaType.setSchema(new Schema<>().type("object"));
        mediaType.setExample(jsonExample.trim());
        content.addMediaType("application/json", mediaType);
        body.setContent(content);
        body.setRequired(true);
        return body;
    }

    /**
     * Creates an ApiResponses object from alternating pairs of HTTP status
     * codes and descriptions.
     *
     * @param statusAndDesc array of status code followed by description (e.g.
     * "200", "OK", "404", "Not Found")
     * @return constructed {@link ApiResponses} instance
     */
    private static ApiResponses createResponses(String... statusAndDesc) {
        ApiResponses responses = new ApiResponses();
        for (int i = 0; i < statusAndDesc.length; i += 2) {
            String code = statusAndDesc[i];
            String desc = (i + 1 < statusAndDesc.length) ? statusAndDesc[i + 1] : "";
            responses.addApiResponse(code, new ApiResponse().description(desc));
        }
        return responses;
    }

    /**
     * Builds and registers all 17 DTO models and the JWT Bearer security scheme
     * into the OpenAPI Components model.
     *
     * @return fully populated {@link Components} registry
     */
    public static Components buildComponents() {
        Components components = new Components();

        // Register global JWT Bearer security scheme
        components.addSecuritySchemes(SECURITY_SCHEME_NAME, new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("JWT Bearer Access Token. Example: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."));

        // 1. Auth Service DTO Schemas
        components.addSchemas("RegisterRequest", buildSchema("Customer user registration request",
                List.of("firstName", "lastName", "email", "password"),
                "firstName", stringSchema("Asha", "Customer first name"),
                "lastName", stringSchema("Khan", "Customer last name"),
                "email", stringSchema("asha.khan@example.com", "Unique email address for authentication"),
                "phoneNumber", stringSchema("9876543210", "10-digit primary contact phone number"),
                "dateOfBirth", stringSchema("1998-05-12", "Date of birth (YYYY-MM-DD)"),
                "nationality", stringSchema("Indian", "Passenger nationality"),
                "password", stringSchema("Asha@12345", "Account security password (min 6 characters)")
        ));

        components.addSchemas("LoginRequest", buildSchema("User credential authentication request",
                List.of("email", "password"),
                "email", stringSchema("asha.khan@example.com", "Registered user email"),
                "password", stringSchema("Asha@12345", "User account password")
        ));

        components.addSchemas("RefreshTokenRequest", buildSchema("Refresh token exchange request",
                List.of("refreshToken"),
                "refreshToken", stringSchema("d9a3b64c-8f4b-4890-a241-119c67bc2f20", "Valid refresh token UUID")
        ));

        components.addSchemas("AuthResponse", buildSchema("Authentication success response with tokens",
                List.of("accessToken", "refreshToken", "tokenType", "role", "userId"),
                "accessToken", stringSchema("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...", "Signed JWT bearer access token"),
                "refreshToken", stringSchema("d9a3b64c-8f4b-4890-a241-119c67bc2f20", "Persistent refresh token"),
                "tokenType", stringSchema("Bearer", "Token authorization prefix scheme"),
                "role", stringSchema("ROLE_USER", "Assigned platform security authority role"),
                "userId", longSchema(1L, "Database customer account identifier")
        ));

        components.addSchemas("CredentialResponse", buildSchema("Security credentials audit response",
                List.of("credentialId", "userId", "email", "role"),
                "credentialId", longSchema(1L, "Primary credential database identifier"),
                "userId", longSchema(1L, "Associated user profile account ID"),
                "email", stringSchema("asha.khan@example.com", "Registered account email address"),
                "role", stringSchema("ROLE_USER", "Active security authority (ROLE_USER or ROLE_ADMIN)"),
                "createdAt", stringSchema("2026-09-14T10:00:00", "Account registration timestamp"),
                "updatedAt", stringSchema("2026-09-14T10:00:00", "Last role / password modification timestamp")
        ));

        // 2. User Service DTO Schemas
        components.addSchemas("UserResponse", buildSchema("Customer profile account model",
                List.of("id", "firstName", "lastName", "email"),
                "id", longSchema(1L, "User profile database primary identifier"),
                "firstName", stringSchema("Asha", "Customer first name"),
                "lastName", stringSchema("Khan", "Customer last name"),
                "email", stringSchema("asha.khan@example.com", "Customer registered email address"),
                "phoneNumber", stringSchema("9876543210", "Phone contact number"),
                "dateOfBirth", stringSchema("1998-05-12", "Date of birth"),
                "nationality", stringSchema("Indian", "Country of nationality"),
                "role", stringSchema("ROLE_USER", "Platform authority role")
        ));

        // 3. Flight Service DTO Schemas
        components.addSchemas("CreateFlightRequest", buildSchema("Commercial flight scheduling request",
                List.of("flightNumber", "airlineName", "source", "destination", "departureTime", "arrivalTime", "aircraftId", "baseFare"),
                "flightNumber", stringSchema("AI101", "Commercial flight code"),
                "airlineName", stringSchema("Air India", "Operating airline carrier"),
                "source", stringSchema("Mumbai", "Departure origin city"),
                "destination", stringSchema("Delhi", "Arrival destination city"),
                "departureTime", stringSchema("2026-10-01T06:00:00", "Scheduled departure timestamp"),
                "arrivalTime", stringSchema("2026-10-01T08:15:00", "Scheduled arrival timestamp"),
                "totalSeats", intSchema(180, "Total passenger seat capacity"),
                "baseFare", numberSchema(4500.00, "Base ticket fare in INR"),
                "aircraftId", longSchema(1L, "Assigned fleet aircraft database ID")
        ));

        components.addSchemas("FlightResponse", buildSchema("Scheduled flight details response",
                List.of("id", "flightNumber", "airlineName", "source", "destination", "departureTime", "arrivalTime", "status"),
                "id", longSchema(1L, "Flight database identifier"),
                "flightNumber", stringSchema("AI101", "Commercial flight code"),
                "airlineName", stringSchema("Air India", "Operating carrier name"),
                "source", stringSchema("Mumbai", "Origin departure city"),
                "destination", stringSchema("Delhi", "Destination arrival city"),
                "departureTime", stringSchema("2026-10-01T06:00:00", "Departure timestamp"),
                "arrivalTime", stringSchema("2026-10-01T08:15:00", "Arrival timestamp"),
                "totalSeats", intSchema(180, "Configured total seat capacity"),
                "availableSeats", intSchema(180, "Currently available open seats for reservation"),
                "baseFare", numberSchema(4500.00, "Standard base economy fare"),
                "status", stringSchema("SCHEDULED", "Operational flight status (SCHEDULED, DELAYED, CANCELLED)")
        ));

        components.addSchemas("Aircraft", buildSchema("Airline fleet aircraft inventory record",
                List.of("id", "aircraftCode", "aircraftName", "capacity"),
                "id", longSchema(1L, "Aircraft database identifier"),
                "aircraftCode", stringSchema("A320-001", "Registration tail code"),
                "aircraftName", stringSchema("Airbus A320", "Commercial aircraft model name"),
                "manufacturer", stringSchema("Airbus", "Aircraft manufacturer"),
                "capacity", intSchema(180, "Maximum passenger seating capacity"),
                "status", stringSchema("ACTIVE", "Fleet operational status")
        ));

        // 4. Fare Service DTO Schemas
        components.addSchemas("FareRequest", buildSchema("Tiered cabin fare pricing configuration",
                List.of("flightId", "economyFare", "businessFare", "firstClassFare", "taxPercentage"),
                "flightId", longSchema(1L, "Scheduled flight database ID"),
                "economyFare", numberSchema(4500.00, "Economy cabin base ticket price"),
                "businessFare", numberSchema(8500.00, "Business class cabin ticket price"),
                "firstClassFare", numberSchema(14000.00, "First class VIP cabin ticket price"),
                "taxPercentage", numberSchema(18.0, "Applicable government GST tax percentage"),
                "discountPercentage", numberSchema(5.0, "Promotional discount percentage"),
                "effectiveDate", stringSchema("2026-10-01", "Fare effective start date")
        ));

        components.addSchemas("FareResponse", buildSchema("Tiered cabin fare details response",
                List.of("id", "flightId", "economyFare", "businessFare", "firstClassFare", "taxPercentage", "active"),
                "id", longSchema(1L, "Fare record primary identifier"),
                "flightId", longSchema(1L, "Associated flight identifier"),
                "economyFare", numberSchema(4500.00, "Economy cabin price"),
                "businessFare", numberSchema(8500.00, "Business cabin price"),
                "firstClassFare", numberSchema(14000.00, "First class cabin price"),
                "taxPercentage", numberSchema(18.0, "GST tax percentage"),
                "discountPercentage", numberSchema(5.0, "Promotional discount percentage"),
                "effectiveDate", stringSchema("2026-10-01", "Effective date"),
                "active", boolSchema(true, "Whether pricing structure is active")
        ));

        // 5. Booking Service DTO Schemas
        components.addSchemas("PassengerRequest", buildSchema("Passenger booking details",
                List.of("firstName", "lastName", "age", "gender"),
                "firstName", stringSchema("Asha", "Passenger first name"),
                "lastName", stringSchema("Khan", "Passenger last name"),
                "age", intSchema(29, "Passenger age in years"),
                "gender", stringSchema("F", "Passenger gender (M/F/O)")
        ));

        components.addSchemas("BookingRequest", buildSchema("Flight reservation creation request",
                List.of("userId", "flightId", "passengers"),
                "userId", longSchema(1L, "Customer user account ID"),
                "flightId", longSchema(1L, "Target flight database ID to book"),
                "passengers", new ArraySchema().items(new Schema<>().$ref("#/components/schemas/PassengerRequest")).description("List of passengers for reservation")
        ));

        components.addSchemas("BookingResponse", buildSchema("Confirmed flight reservation response",
                List.of("id", "userId", "flightId", "pnr", "totalFare", "status"),
                "id", longSchema(1L, "Booking record primary ID"),
                "userId", longSchema(1L, "Customer account identifier"),
                "flightId", longSchema(1L, "Scheduled flight identifier"),
                "pnr", stringSchema("PNR-D98D46AC", "Unique 8-character Passenger Name Record code"),
                "bookingDate", stringSchema("2026-09-14T15:30:00", "Reservation confirmation timestamp"),
                "totalFare", numberSchema(5310.00, "Total fare paid including taxes and discounts"),
                "status", stringSchema("CONFIRMED", "Booking status (CONFIRMED, CANCELLED)"),
                "passengers", new ArraySchema().items(new Schema<>().$ref("#/components/schemas/PassengerRequest")).description("Manifest of passengers booked")
        ));

        // 6. Check-In Service DTO Schemas
        components.addSchemas("CheckInRequest", buildSchema("Passenger airport check-in request",
                List.of("bookingId", "passengerName"),
                "bookingId", longSchema(1L, "Confirmed flight booking database ID"),
                "passengerName", stringSchema("Asha Khan", "Full passenger name as on reservation"),
                "seatNumber", stringSchema("12A", "Selected seat identifier (e.g. 12A, 14B)")
        ));

        components.addSchemas("CheckIn", buildSchema("Issued boarding pass and check-in confirmation",
                List.of("id", "bookingId", "passengerName", "seatNumber", "boardingPassNumber", "status"),
                "id", longSchema(1L, "Check-in confirmation primary ID"),
                "bookingId", longSchema(1L, "Associated booking ID"),
                "passengerName", stringSchema("Asha Khan", "Passenger name"),
                "seatNumber", stringSchema("12A", "Assigned aircraft seat number"),
                "boardingPassNumber", stringSchema("BP-1-12A", "Generated barcode boarding pass identifier"),
                "status", stringSchema("CHECKED_IN", "Confirmation status"),
                "checkedInAt", stringSchema("2026-09-14T15:30:00", "Airport check-in completion timestamp")
        ));

        // 7. Error Response Model
        components.addSchemas("ErrorResponse", buildSchema("Standardized error response model",
                List.of("timestamp", "status", "error", "message"),
                "timestamp", stringSchema("2026-09-14T15:30:00", "Error occurrence timestamp"),
                "status", intSchema(403, "HTTP response status code"),
                "error", stringSchema("Forbidden", "HTTP status reason phrase"),
                "message", stringSchema("Access denied: Requires ROLE_ADMIN authority.", "Detailed descriptive error message")
        ));

        return components;
    }

    /**
     * Constructs a String property Schema with example value and description.
     */
    private static Schema<String> stringSchema(String example, String desc) {
        Schema<String> s = new Schema<>();
        s.setType("string");
        s.setExample(example);
        s.setDescription(desc);
        return s;
    }

    /**
     * Constructs a 32-bit Integer property Schema with example value and
     * description.
     */
    private static Schema<Integer> intSchema(int example, String desc) {
        Schema<Integer> s = new Schema<>();
        s.setType("integer");
        s.setExample(example);
        s.setDescription(desc);
        return s;
    }

    /**
     * Constructs a 64-bit Long property Schema with example value and
     * description.
     */
    private static Schema<Long> longSchema(long example, String desc) {
        Schema<Long> s = new Schema<>();
        s.setType("integer");
        s.setFormat("int64");
        s.setExample(example);
        s.setDescription(desc);
        return s;
    }

    /**
     * Constructs a Double floating-point property Schema with example value and
     * description.
     */
    private static Schema<Double> numberSchema(double example, String desc) {
        Schema<Double> s = new Schema<>();
        s.setType("number");
        s.setFormat("double");
        s.setExample(example);
        s.setDescription(desc);
        return s;
    }

    /**
     * Constructs a Boolean property Schema with example value and description.
     */
    private static Schema<Boolean> boolSchema(boolean example, String desc) {
        Schema<Boolean> s = new Schema<>();
        s.setType("boolean");
        s.setExample(example);
        s.setDescription(desc);
        return s;
    }

    /**
     * Helper to assemble an Object Schema from alternating property names and
     * property Schemas.
     *
     * @param description model description
     * @param required list of mandatory field names
     * @param propPairs key-value pairs alternating between String name and
     * Schema<?> object
     * @return constructed Object {@link Schema}
     */
    private static Schema<Object> buildSchema(String description, List<String> required, Object... propPairs) {
        Schema<Object> schema = new Schema<>();
        schema.setType("object");
        schema.setDescription(description);
        if (required != null && !required.isEmpty()) {
            schema.setRequired(required);
        }
        for (int i = 0; i < propPairs.length; i += 2) {
            String name = (String) propPairs[i];
            Schema<?> propSchema = (Schema<?>) propPairs[i + 1];
            schema.addProperty(name, propSchema);
        }
        return schema;
    }
}
