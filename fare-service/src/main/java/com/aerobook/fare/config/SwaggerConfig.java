package com.aerobook.fare.config;

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
 * OpenAPI 3 / Swagger Configuration for Aerobook Fare & Pricing Service.
 */
@Configuration
public class SwaggerConfig {

    private static final String SECURITY_SCHEME_NAME = "BearerAuth";

    @Bean
    public OpenAPI fareServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("💰 Aerobook Fare & Pricing Service (Port 8089)")
                        .version("1.0.0 (Enterprise Business Edition)")
                        .description("""
                                # 💵 Multi-tier Cabin Pricing & Revenue Management
                                
                                The **Fare Service** controls real-time flight ticket pricing models, 
                                multi-cabin pricing structures, tax regulations, and dynamic promotional discounts.
                                
                                ---
                                
                                ### 📋 Service Summary
                                - **Service Port**: `8089`
                                - **Database**: `aerobook_fare_db` (MySQL 8)
                                - **Cabin Tiers**: Economy Class, Business Class, First Class
                                - **Financial Calculations**: Taxes, surcharge percentages, and promotional discounts
                                
                                ---
                                
                                ### 🔐 Role-Based Access Control (RBAC) Matrix
                                
                                | Endpoint | HTTP Method | Allowed Roles | Description |
                                | :--- | :--- | :--- | :--- |
                                | `/api/fares/health` | `GET` | **Public** | Service health and liveness probe |
                                | `/api/fares/{id}` | `GET` | `ROLE_USER`, `ROLE_ADMIN` | Retrieve pricing breakdown by Fare ID |
                                | `/api/fares/flight/{flightId}` | `GET` | `ROLE_USER`, `ROLE_ADMIN` | Retrieve active fare configuration for a flight |
                                | `/api/fares` | `GET` | `ROLE_USER`, `ROLE_ADMIN` | List all flight fares in the system |
                                | `/api/fares` | `POST` | 🔴 **`ROLE_ADMIN` Only** | Configure tiered pricing for a flight |
                                | `/api/fares/{id}` | `PUT` | 🔴 **`ROLE_ADMIN` Only** | Update fare prices, discounts, or taxes |
                                | `/api/fares/{id}` | `DELETE` | 🔴 **`ROLE_ADMIN` Only** | Permanently delete a fare configuration |
                                
                                ---
                                
                                ### 🛡️ Implemented Business Validations
                                1. **Price Integrity**: All cabin fares (Economy, Business, First Class) must be non-negative (`>= 0.0`).
                                2. **Tax Bounds**: Tax percentage must be between `0.0%` and `100.0%`.
                                3. **Discount Bounds**: Promotional discounts must be between `0.0%` and `100.0%`.
                                4. **Flight Linkage**: Each fare is strictly linked to a valid flight database ID.
                                
                                ---
                                
                                ### 🧪 Testing Guide for Presentation:
                                1. **Authorize**: Click the green **Authorize 🔓** button and paste your JWT access token.
                                2. **Check Health**: Execute `GET /api/fares/health` without a token.
                                3. **Configure Fare**: Open `POST /api/fares`. The request body is **pre-populated** with pricing for `flightId: 1` (Economy ₹4500, Business ₹8500, First Class ₹14000, Tax 18%). Click **Execute** $\rightarrow$ `201 Created`.
                                4. **Query by Flight ID**: Test `GET /api/fares/flight/1` to view the active pricing.
                                5. **Admin RBAC Verification**: Attempting `POST /api/fares` with a non-admin token will return `403 Forbidden`.
                                """)
                        .contact(new Contact()
                                .name("Aerobook Revenue Management")
                                .email("fares@aerobook.com")
                                .url("https://aerobook.com"))
                        .license(new License()
                                .name("Academic Evaluation License - Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                .servers(List.of(
                        new Server().url("http://localhost:8083").description("Aerobook API Gateway (Recommended)"),
                        new Server().url("http://localhost:8089").description("Direct Fare Service Instance (:8089)")
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
