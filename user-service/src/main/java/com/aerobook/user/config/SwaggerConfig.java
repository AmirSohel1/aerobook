package com.aerobook.user.config;

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
 * OpenAPI 3 / Swagger configuration for Aerobook User Service. Provides rich
 * metadata, interactive testing documentation, and RBAC matrix.
 */
@Configuration
public class SwaggerConfig {

    private static final String SECURITY_SCHEME_NAME = "BearerAuth";

    @Bean
    public OpenAPI userServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("👤 Aerobook User Service (Port 8084)")
                        .version("1.0.0 (Production Business-Ready Release)")
                        .description("""
                                # 👤 Customer Profiles, Demographics & Account Management
                                
                                The **User Service** is the central repository for passenger identity, contact information,
                                and demographic records within the distributed Aerobook cloud architecture.
                                
                                ---
                                
                                ### 📋 Service Summary
                                - **Service Port**: `8084`
                                - **Database**: `aerobook_user_db` (MySQL 8)
                                - **Authentication Protocol**: JWT (JSON Web Token) via API Gateway (`port 8083`)
                                - **Inter-Service Clients**: Invoked directly via OpenFeign by `auth-service` during user registration
                                
                                ---
                                
                                ### 🔐 Role-Based Access Control (RBAC) Matrix
                                
                                | Endpoint | Method | Required Authority | Description |
                                | :--- | :--- | :--- | :--- |
                                | `/api/v1/users/health` | `GET` | **Public** | Operational readiness health check |
                                | `/api/v1/users/{id}` | `GET` | `ROLE_USER` / `ROLE_ADMIN` | View individual account profile by ID |
                                | `/api/v1/users/email/{email}` | `GET` | `ROLE_USER` / `ROLE_ADMIN` | Lookup user profile by registered email |
                                | `/api/v1/users/{id}` | `PUT` | `ROLE_USER` / `ROLE_ADMIN` | Modify profile details (Self or Admin) |
                                | `/api/v1/users` | `GET` | 🔴 **`ROLE_ADMIN` Only** | Enumerate all user accounts in database |
                                | `/api/v1/users` | `POST` | 🔴 **`ROLE_ADMIN` Only** | Direct administrative profile creation |
                                | `/api/v1/users/{id}` | `DELETE` | 🔴 **`ROLE_ADMIN` Only** | Permanently remove a user account |
                                
                                ---
                                
                                ### 🧪 Interactive Testing Guide for Teacher Presentation:
                                1. **Authorize Token**:
                                   - Click the green **Authorize** button at the top right.
                                   - Paste a valid JWT token generated from `POST /api/auth/login` or `POST /api/auth/register-admin`.
                                   - Click **Authorize** and then **Close**.
                                2. **Retrieve Profile by ID**:
                                   - Expand `GET /api/v1/users/{id}`.
                                   - Click **Try it out**, specify `id = 1`, and click **Execute**.
                                3. **Retrieve Profile by Email**:
                                   - Expand `GET /api/v1/users/email/{email}`.
                                   - Click **Try it out**, enter the email (e.g. `ravi.patel@example.com`), and click **Execute**.
                                4. **Update Profile**:
                                   - Expand `PUT /api/v1/users/{id}`.
                                   - Pre-filled sample data is provided. Click **Execute** to update fields.
                                5. **Admin List All Users**:
                                   - Expand `GET /api/v1/users`.
                                   - Execute with `ROLE_ADMIN` token to view all registered users. Non-admin tokens will receive `403 Forbidden`.
                                """)
                        .contact(new Contact()
                                .name("Aerobook Customer Identity Team")
                                .email("users@aerobook.com")
                                .url("https://aerobook.com"))
                        .license(new License()
                                .name("Apache 2.0 Evaluation License")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                .servers(List.of(
                        new Server().url("http://localhost:8083").description("Aerobook API Gateway (:8083 - Recommended Gateway Proxy)"),
                        new Server().url("http://localhost:8084").description("Direct User Service Instance (:8084 - Standalone)")
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
