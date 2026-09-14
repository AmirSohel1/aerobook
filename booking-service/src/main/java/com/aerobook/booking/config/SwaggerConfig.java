package com.aerobook.booking.config;

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
 * OpenAPI 3 / Swagger Configuration for Aerobook Booking Service. Provides
 * interactive documentation, RBAC matrix, and pre-populated test data.
 */
@Configuration
public class SwaggerConfig {

    private static final String SECURITY_SCHEME_NAME = "BearerAuth";

    @Bean
    public OpenAPI bookingServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("🎫 Aerobook Booking Service (Port 8088)")
                        .version("1.0.0 (Enterprise Business Edition)")
                        .description("""
                                # 🎟️ Flight Reservations, PNR Engine & Lifecycle Events
                                
                                The **Booking Service** coordinates passenger reservations, enforces airline business rules
                                (departure date validation, seat availability, flight status checks), generates unique PNR codes,
                                and publishes asynchronous booking lifecycle events to RabbitMQ.
                                
                                ---
                                
                                ### 📋 Service Summary
                                - **Service Port**: `8088`
                                - **Database**: `aerobook_booking_db` (MySQL 8)
                                - **Inter-Service Communication**: OpenFeign client communicates dynamically with `FLIGHT-SERVICE` via Eureka
                                - **Message Broker Integration**: RabbitMQ (`guest`/`guest` on port `5672`), emits `BOOKING_CREATED:<pnr>` to queue `booking-created`
                                - **PNR Format**: Alphanumeric booking reference code (e.g. `AB1001`)
                                
                                ---
                                
                                ### 🔐 Role-Based Access Control (RBAC) Matrix
                                
                                | Endpoint | HTTP Method | Allowed Roles | Description |
                                | :--- | :--- | :--- | :--- |
                                | `/api/bookings/health` | `GET` | **Public** | Service liveness and health probe |
                                | `/api/bookings` | `POST` | `ROLE_USER`, `ROLE_ADMIN` | Reserve flight seats with full validation |
                                | `/api/bookings/{bookingId}` | `GET` | `ROLE_USER`, `ROLE_ADMIN` | Lookup reservation by Database ID |
                                | `/api/bookings/pnr/{pnr}` | `GET` | `ROLE_USER`, `ROLE_ADMIN` | Retrieve booking details by PNR |
                                | `/api/bookings/user/{userId}` | `GET` | `ROLE_USER`, `ROLE_ADMIN` | List all bookings for a user |
                                | `/api/bookings` | `GET` | **`ROLE_ADMIN`** | Administrative view of all airline bookings |
                                | `/api/bookings/{bookingId}` | `DELETE` | `ROLE_USER`, `ROLE_ADMIN` | Cancel a reservation (safeguarded) |
                                
                                ---
                                
                                ### 🛡️ Implemented Business Validations
                                1. **Departure Date Validation**: Rejects bookings if the flight's scheduled departure time has already passed.
                                2. **Flight Status Guard**: Blocks bookings for flights marked as `CANCELLED` or `COMPLETED`.
                                3. **Seat Availability Check**: Ensures the flight has sufficient `availableSeats` to accommodate the requested party size.
                                4. **Cancellation Safeguard**: Prevents duplicate cancellations and validates existing booking records.
                                
                                ---
                                
                                ### 🧪 Testing Guide for Presentation:
                                1. **Authorize**: Click the **Authorize** button (top-right) and enter your JWT token (`Bearer <token>`).
                                2. **Check Health**: Execute `GET /api/bookings/health` without a token.
                                3. **Create Booking**: Open `POST /api/bookings`. The request body is pre-populated with passenger **Asha Khan** for `flightId: 1` and `userId: 1`. Click **Execute**.
                                4. **Inspect Response**: View the confirmed booking, calculated total fare, and generated **PNR** (e.g. `AB1001`).
                                5. **Lookup by PNR**: Call `GET /api/bookings/pnr/{pnr}` using your generated PNR.
                                6. **Admin List All**: Call `GET /api/bookings` with an admin JWT token to review all reservations.
                                """)
                        .contact(new Contact()
                                .name("Aerobook Booking Operations")
                                .email("bookings@aerobook.com")
                                .url("https://aerobook.com"))
                        .license(new License()
                                .name("Academic Evaluation License - Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                .servers(List.of(
                        new Server().url("http://localhost:8083").description("Aerobook API Gateway (Recommended)"),
                        new Server().url("http://localhost:8088").description("Direct Booking Service Instance (:8088)")
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
