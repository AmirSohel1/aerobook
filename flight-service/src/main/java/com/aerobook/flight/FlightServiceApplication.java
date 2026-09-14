package com.aerobook.flight;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Main entry point for the AeroBook Flight Service. Manages aircraft fleets,
 * commercial flight schedules, seat inventory, and dynamic searches. Registers
 * with Netflix Eureka and enables OpenFeign declarative HTTP clients.
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class FlightServiceApplication {

    /**
     * Bootstraps the Spring Boot application context.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(FlightServiceApplication.class, args);
    }

}
