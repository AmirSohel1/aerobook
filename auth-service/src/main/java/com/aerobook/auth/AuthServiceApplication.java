package com.aerobook.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * ============================================================================
 * Aerobook Cloud Airline Platform - Authentication Service (Port 8082)
 * ============================================================================
 *
 * Central security and identity authority for the Aerobook distributed
 * platform.
 *
 * <p>
 * Key Architecture Responsibilities:
 * <ul>
 * <li><b>User Registration:</b> Coordinates customer account registration,
 * hashes passwords using BCrypt, and delegates customer profile creation to
 * User Service via {@link com.aerobook.auth.client.UserClient}.</li>
 * <li><b>Master Admin Setup:</b> Supports secure administrative registration
 * guarded by the secret setup key ({@code AerobookAdminSetup2026}).</li>
 * <li><b>JWT Token Lifecycle:</b> Issues, verifies, and refreshes HMAC-SHA256
 * signed access tokens and persistent UUID refresh tokens.</li>
 * <li><b>Role-Based Access Control:</b> Enforces separation between standard
 * customers ({@code ROLE_USER}) and administrators ({@code ROLE_ADMIN}).</li>
 * <li><b>Credential Oversight:</b> Provides administrative credential audits
 * and security role elevation endpoints.</li>
 * </ul>
 *
 * @author Aerobook Security Engineering
 * @version 1.0.0
 */
@SpringBootApplication
@EnableFeignClients
public class AuthServiceApplication {

    /**
     * Application entry point for starting the Auth Service microservice.
     *
     * @param args command-line arguments passed to the JVM
     */
    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
    }
}
