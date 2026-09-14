package com.aerobook.checkin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * ============================================================================
 * Aerobook Airport Check-In & Boarding Pass Microservice
 * ============================================================================
 *
 * Spring Boot microservice responsible for passenger check-in verification,
 * physical seat allocation, and boarding pass generation (e.g., {@code BP-1-12A}).
 * Operates on port {@code 8090} and integrates with Netflix Eureka discovery.
 *
 * @author Aerobook Platform Engineering
 * @version 1.0.0
 */
@SpringBootApplication
@EnableDiscoveryClient
public class CheckInServiceApplication {

    /**
     * Bootstraps the Check-In microservice application context on port 8090.
     *
     * @param args runtime command-line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(CheckInServiceApplication.class, args);
    }

}
