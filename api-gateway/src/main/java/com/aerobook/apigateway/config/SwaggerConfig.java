package com.aerobook.apigateway.config;

import java.util.List;

import org.springdoc.core.customizers.GlobalOpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.servers.Server;

/**
 * ============================================================================
 * Centralized OpenAPI 3 / Swagger Configuration for Aerobook API Gateway
 * ============================================================================
 *
 * Configures the primary OpenAPI bean and customization hooks for SpringDoc
 * OpenAPI running on Spring Cloud Gateway Reactive WebFlux.
 *
 * <p>
 * Key Features:
 * <ul>
 * <li><b>Centralized Documentation:</b> Aggregates all 6 downstream
 * microservices into a single documentation hub with full endpoint
 * descriptions, status codes, and pre-filled request payloads.</li>
 * <li><b>Reverse-Proxy Execution:</b> Configures {@code http://localhost:8083}
 * as the target server so clicking "Try it out" $\rightarrow$ "Execute" routes
 * live traffic through the Gateway filter chain.</li>
 * <li><b>Security Integration:</b> Defines global HTTP Bearer JWT security
 * scheme ({@code BearerAuth}) allowing persistent authentication across all
 * test requests.</li>
 * <li><b>Teacher Presentation Guide:</b> Provides embedded walkthrough
 * instructions for demonstrating the system during final academic/project
 * evaluations.</li>
 * </ul>
 *
 * @author Aerobook Platform Engineering
 * @version 1.0.0
 */
@Configuration
public class SwaggerConfig {

    /**
     * Standard scheme identifier used across OpenAPI security requirements.
     */
    private static final String SECURITY_SCHEME_NAME = "BearerAuth";

    /**
     * Primary OpenAPI bean for the central API Gateway. Populates all platform
     * tags, paths, info metadata, reverse proxy server, security scheme, and
     * all 17 DTO component schemas.
     *
     * @return fully configured {@link OpenAPI} instance for the complete
     * platform view
     */
    @Bean
    public OpenAPI gatewayOpenAPI() {
        return new OpenAPI()
                .tags(GatewayOpenApiPathsBuilder.getPlatformTags())
                .paths(GatewayOpenApiPathsBuilder.buildAllPaths())
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
                .components(GatewayOpenApiPathsBuilder.buildComponents());
    }

    /**
     * Customizer bean invoked by SpringDoc to ensure dynamically calculated
     * platform paths, tags, and components are always synchronized with the
     * central OpenAPI definition.
     *
     * @return a {@link GlobalOpenApiCustomizer} lambda modifying the active
     * OpenAPI instance
     */
    @Bean
    public GlobalOpenApiCustomizer platformGlobalOpenApiCustomizer() {
        return openApi -> {
            openApi.setTags(GatewayOpenApiPathsBuilder.getPlatformTags());
            Paths allPaths = GatewayOpenApiPathsBuilder.buildAllPaths();
            if (openApi.getPaths() != null) {
                openApi.getPaths().forEach(allPaths::addPathItem);
            }
            openApi.setPaths(allPaths);
            openApi.setComponents(GatewayOpenApiPathsBuilder.buildComponents());
        };
    }
}
