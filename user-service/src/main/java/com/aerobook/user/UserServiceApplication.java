package com.aerobook.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * ============================================================================
 * Aerobook User Profile Management Microservice
 * ============================================================================
 *
 * Primary entry point for the User Profile Service running on port
 * {@code 8084}.
 *
 * <p>
 * Key Responsibilities:
 * <ul>
 * <li>Customer profile lifecycle management (demographics, contact information,
 * DOB)</li>
 * <li>Administrative account oversight and directory management</li>
 * <li>Eureka service discovery client integration for inter-service
 * lookups</li>
 * <li>OpenFeign client integration for cross-boundary communication</li>
 * </ul>
 *
 * @author Aerobook Platform Engineering
 * @version 1.0.0
 */
@SpringBootApplication
@EnableFeignClients
@EnableDiscoveryClient
public class UserServiceApplication {

    /**
     * Bootstraps the User Service Spring application context on port 8084.
     *
     * @param args command-line startup arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }

}
