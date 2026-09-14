package com.aerobook.flight.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Enhanced OpenAPI 3 / Swagger Configuration for Aerobook Flight & Fleet
 * Service. Provides interactive testing guides, RBAC permission matrix, and
 * full schema definitions.
 */
@Configuration
public class SwaggerConfig {

    private static final String SECURITY_SCHEME_NAME = "BearerAuth";

    /**
     * Constructs the custom OpenAPI bean providing comprehensive API
     * documentation, Swagger UI role descriptions, interactive testing flows,
     * and Bearer authentication.
     *
     * @return populated {@link OpenAPI} specification model
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("✈️ Aerobook Flight & Fleet Service (Port 8087)")
                        .version("1.0.0 (Production Business-Ready Release)")
                        .description("""
                                # 🛫 Flight Operations, Route Search Engine & Fleet Management
                                
                                The **Flight Service** coordinates commercial aircraft inventory, scheduled routes, 
                                real-time flight search, seat capacity allocations, and asynchronous event messaging.
                                
                                ---
                                
                                ### 📋 Service Summary
                                - **Service Port**: `8087`
                                - **Database**: `aerobook_flight_db` (MySQL 8)
                                - **Message Broker Integration**: RabbitMQ listener synchronizing seat availability on `BOOKING_CREATED` events
                                - **Inter-Service Feign API**: Exposes `GET /api/flights/{id}` for Booking Service fare resolution
                                
                                ---
                                
                                ### 🔐 Role-Based Access Control (RBAC) Matrix
                                
                                | Endpoint | Method | Required Authority | Description |
                                | :--- | :--- | :--- | :--- |
                                | `/api/flights/health` | `GET` | **Public** | Operational readiness health check |
                                | `/api/flights/search` | `GET` | `ROLE_USER` / `ROLE_ADMIN` | Search active flights by departure & arrival cities |
                                | `/api/flights` | `GET` | `ROLE_USER` / `ROLE_ADMIN` | Browse complete scheduled flights catalog |
                                | `/api/flights/{id}` | `GET` | `ROLE_USER` / `ROLE_ADMIN` | View flight details and seat availability |
                                | `/api/admin/aircrafts` | `POST` | 🔴 **`ROLE_ADMIN` Only** | Register new aircraft model into fleet |
                                | `/api/admin/aircrafts` | `GET` | 🔴 **`ROLE_ADMIN` Only** | View fleet inventory and capacities |
                                | `/api/admin/aircrafts/{id}`| `GET` / `DELETE` | 🔴 **`ROLE_ADMIN` Only** | Inspect or remove fleet aircraft |
                                | `/api/admin/flights` | `POST` | 🔴 **`ROLE_ADMIN` Only** | Schedule a new commercial flight |
                                | `/api/admin/flights/{id}`| `PUT` / `DELETE` | 🔴 **`ROLE_ADMIN` Only** | Reschedule, update timings, or cancel a flight |
                                | `/api/admin/flights` | `GET` | 🔴 **`ROLE_ADMIN` Only** | Admin oversight of all flight schedules |
                                
                                ---
                                
                                ### 🧪 Interactive Testing Guide for Teacher Presentation:
                                1. **Authorize Token**:
                                   - Click the green **Authorize** button at the top right.
                                   - Paste a valid JWT token generated from Auth Service (with `ROLE_ADMIN`).
                                   - Click **Authorize** and then **Close**.
                                2. **Step 1: Register Fleet Aircraft (Admin)**:
                                   - Open `POST /api/admin/aircrafts`.
                                   - Click **Try it out** $\rightarrow$ Observe pre-populated Airbus A320 (`A320-001`, capacity 180) $\rightarrow$ Click **Execute**.
                                3. **Step 2: Schedule Flight AI101 (Admin)**:
                                   - Open `POST /api/admin/flights`.
                                   - Click **Try it out** $\rightarrow$ Pre-filled sample schedules `AI101` from `Mumbai` to `Delhi` linked to `aircraftId: 1` $\rightarrow$ Click **Execute**.
                                4. **Step 3: Search Flights by Route (Customer)**:
                                   - Open `GET /api/flights/search`.
                                   - Click **Try it out**, set `source = Mumbai` and `destination = Delhi` $\rightarrow$ Click **Execute**.
                                5. **Step 4: Browse Complete Flight Catalog**:
                                   - Open `GET /api/flights` $\rightarrow$ Click **Try it out** $\rightarrow$ **Execute**.
                                """)
                        .contact(new Contact()
                                .name("Aerobook Flight Operations")
                                .email("flight-ops@aerobook.com")
                                .url("https://aerobook.com"))
                        .license(new License()
                                .name("Apache 2.0 Evaluation License")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                .servers(List.of(
                        new Server().url("http://localhost:8083").description("Aerobook API Gateway (:8083 - Recommended Gateway Proxy)"),
                        new Server().url("http://localhost:8087").description("Direct Flight Service Instance (:8087 - Standalone)")
                ))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME, new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Paste JWT access token here to authenticate secured calls.")));
    }
}
