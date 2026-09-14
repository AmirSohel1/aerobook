package com.aerobook.auth.config;

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
 * ============================================================================
 * OpenAPI 3 / Swagger Documentation Configuration for Auth Service
 * ============================================================================
 *
 * Configures interactive Swagger UI and OpenAPI 3 specifications for the
 * Authentication Service (Port 8082).
 *
 * <p>
 * Key Configuration Elements:
 * <ul>
 * <li><b>Security Scheme:</b> Global HTTP Bearer JWT token scheme
 * ({@code BearerAuth}).</li>
 * <li><b>Server Routing:</b> Supports execution through API Gateway
 * ({@code http://localhost:8083}) as well as direct container access
 * ({@code http://localhost:8082}).</li>
 * <li><b>RBAC Documentation Matrix:</b> Detailed endpoint authority policy and
 * teacher demo script.</li>
 * </ul>
 *
 * @author Aerobook Security Engineering
 * @version 1.0.0
 */
@Configuration
public class SwaggerConfig {

    /**
     * Name of the JWT Bearer security scheme definition.
     */
    private static final String SECURITY_SCHEME_NAME = "BearerAuth";

    /**
     * Configures the primary OpenAPI document with service info, JWT security
     * scheme, dual server environments, and testing walkthrough instructions.
     *
     * @return populated {@link OpenAPI} specification
     */
    @Bean
    public OpenAPI authServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("🔐 Aerobook Auth Service (Port 8082)")
                        .version("1.0.0 (Enterprise Business Edition)")
                        .description("""
                                # 🛡️ Identity, Authentication & Role Management
                                
                                The **Auth Service** is the central security authority of the Aerobook cloud airline platform.
                                It coordinates customer registrations, secure password hashing using **BCrypt**, 
                                administrator setup via protected master keys, **JWT (JSON Web Token)** lifecycle management,
                                and platform-wide user role assignments (`ROLE_USER` vs `ROLE_ADMIN`).
                                
                                ---
                                
                                ### 📋 Service Summary
                                - **Service Port**: `8082`
                                - **Database**: `aerobook_auth_db` (MySQL 8)
                                - **Token Standard**: JWT (HMAC-SHA256, JJWT 0.12.7)
                                - **Access Token Expiration**: 3,600,000 ms (1 Hour)
                                - **Refresh Token Expiration**: 604,800,000 ms (7 Days)
                                - **Master Admin Setup Key**: `AerobookAdminSetup2026`
                                
                                ---
                                
                                ### 🔐 Role-Based Access Control (RBAC) Matrix
                                
                                | Endpoint | HTTP Method | Required Authorization | Description |
                                | :--- | :--- | :--- | :--- |
                                | `/api/auth/health` | `GET` | **Public** | Service liveness and health probe |
                                | `/api/auth/register` | `POST` | **Public** | Register new customer account (`ROLE_USER`) |
                                | `/api/auth/register-admin` | `POST` | **`X-Admin-Setup-Key`** | Register or promote account to `ROLE_ADMIN` |
                                | `/api/auth/login` | `POST` | **Public** | Verify credentials & issue JWT tokens |
                                | `/api/auth/refresh-token` | `POST` | **Public** | Exchange refresh token for new access token |
                                | `/api/auth/credentials` | `GET` | 🔴 **`ROLE_ADMIN` Only** | List all registered user credentials and roles |
                                | `/api/auth/credentials/{userId}/role` | `PUT` | 🔴 **`ROLE_ADMIN` Only** | Promote or demote user authority |
                                
                                ---
                                
                                ### 🧪 Testing Instructions for Teacher Presentation:
                                1. **Register Admin**: Open `POST /api/auth/register-admin`. The request header `X-Admin-Setup-Key: AerobookAdminSetup2026` and payload are **pre-populated**. Click **Execute** $\\rightarrow$ `201 Created`.
                                2. **Copy Token**: From the response, copy the `accessToken`.
                                3. **Authorize**: Click the green **Authorize 🔓** button at the top right, paste the token, and click **Authorize**.
                                4. **Register Customer**: Open `POST /api/auth/register`. Click **Execute** to register customer **Asha Khan** $\\rightarrow$ `201 Created`.
                                5. **Login Test**: Open `POST /api/auth/login`. Click **Execute** with pre-populated credentials to receive new tokens.
                                6. **Admin View Credentials**: Call `GET /api/auth/credentials` with your admin token to inspect all platform user credentials and roles.
                                7. **Admin Role Promotion**: Use `PUT /api/auth/credentials/{userId}/role?role=ROLE_ADMIN` to promote any user account.
                                """)
                        .contact(new Contact()
                                .name("Aerobook Security Engineering")
                                .email("security@aerobook.com")
                                .url("https://aerobook.com"))
                        .license(new License()
                                .name("Academic Evaluation License - Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                .servers(List.of(
                        new Server().url("http://localhost:8083").description("Aerobook API Gateway (Recommended)"),
                        new Server().url("http://localhost:8082").description("Direct Auth Service Instance (:8082)")
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
