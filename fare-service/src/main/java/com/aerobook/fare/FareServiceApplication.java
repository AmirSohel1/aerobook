package com.aerobook.fare;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * ============================================================================
 * Aerobook Fare & Revenue Management Microservice
 * ============================================================================
 *
 * Spring Boot microservice responsible for tiered cabin pricing (Economy,
 * Premium Economy, Business, First Class), GST tax assessments, promotional
 * discounts, and pricing lookups. Operates on port {@code 8089}.
 *
 * <p>Key Architecture Capabilities:
 * <ul>
 *   <li>Multi-tier cabin price schedules per scheduled flight</li>
 *   <li>OpenFeign declarative HTTP client invoking Flight Service</li>
 *   <li>Netflix Eureka service discovery integration</li>
 * </ul>
 *
 * @author Aerobook Platform Engineering
 * @version 1.0.0
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class FareServiceApplication {

    /**
     * Bootstraps the Fare Service Spring context on port 8089.
     *
     * @param args command-line startup parameters
     */
    public static void main(String[] args) {
        SpringApplication.run(FareServiceApplication.class, args);
    }

}
