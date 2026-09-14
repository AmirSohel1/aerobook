package com.aerobook.checkin.config;

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
 * OpenAPI 3 / Swagger Configuration for Aerobook Check-In Service.
 */
@Configuration
public class SwaggerConfig {

    private static final String SECURITY_SCHEME_NAME = "BearerAuth";

    @Bean
    public OpenAPI checkInServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("🧳 Aerobook Check-In Service (Port 8090)")
                        .version("1.0.0 (Enterprise Business Edition)")
                        .description("""
                                # 🎫 Passenger Boarding Pass & Airport Check-In Management
                                
                                The **Check-In Service** manages the passenger airport check-in workflow, 
                                seat assignment, boarding pass generation, and check-in confirmation timestamps.
                                
                                ---
                                
                                ### 📋 Service Summary
                                - **Service Port**: `8090`
                                - **Database**: `aerobook_checkin_db` (MySQL 8)
                                - **Boarding Pass Format**: Alphanumeric format `BP-<bookingId>-<seatNumber>` (e.g. `BP-1-12A`)
                                - **Status Transition**: Updates reservation state to `CHECKED_IN`
                                - **Timestamp Auditing**: Captures exact check-in time (`checkedInAt`)
                                
                                ---
                                
                                ### 🔐 Role-Based Access Control (RBAC) Matrix
                                
                                | Endpoint | HTTP Method | Allowed Roles | Description |
                                | :--- | :--- | :--- | :--- |
                                | `/api/check-ins/health` | `GET` | **Public** | Service liveness and health probe |
                                | `/api/check-ins` | `POST` | `ROLE_USER`, `ROLE_ADMIN` | Check-in passenger & issue boarding pass |
                                | `/api/check-ins/booking/{bookingId}` | `GET` | `ROLE_USER`, `ROLE_ADMIN` | Retrieve check-in record by booking ID |
                                | `/api/check-ins/{id}` | `GET` | `ROLE_USER`, `ROLE_ADMIN` | Retrieve check-in confirmation by primary ID |
                                | `/api/check-ins` | `GET` | 🔴 **`ROLE_ADMIN` Only** | Administrative audit of all airline check-ins |
                                
                                ---
                                
                                ### 🧪 Testing Instructions for Teacher Presentation:
                                1. Ensure you have authorized Swagger UI with a valid JWT token via the **Authorize** button.
                                2. **Check-In Passenger**: Open `POST /api/check-ins`. The request body is **pre-populated** with:
                                   - `bookingId`: `1`
                                   - `passengerName`: `Asha Khan`
                                   - `seatNumber`: `12A`
                                   Click **Execute** $\rightarrow$ `200 OK` with boarding pass `BP-1-12A` and timestamp.
                                3. **Query Check-In Record**: Test `GET /api/check-ins/booking/1` to verify persisted check-in data.
                                4. **Admin Audit**: Call `GET /api/check-ins` with an admin token to review all check-in records across the fleet.
                                """)
                        .contact(new Contact()
                                .name("Aerobook Ground Operations")
                                .email("checkin@aerobook.com")
                                .url("https://aerobook.com"))
                        .license(new License()
                                .name("Academic Evaluation License - Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                .servers(List.of(
                        new Server().url("http://localhost:8083").description("Aerobook API Gateway (Recommended)"),
                        new Server().url("http://localhost:8090").description("Direct Check-In Service Instance (:8090)")
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
