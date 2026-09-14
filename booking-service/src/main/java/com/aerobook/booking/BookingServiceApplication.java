package com.aerobook.booking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * ============================================================================
 * Aerobook Flight Booking & Reservation Microservice
 * ============================================================================
 *
 * Primary entry point for the Booking Service running on port {@code 8088}.
 *
 * <p>Key System Functions:
 * <ul>
 *   <li>Flight seat reservation and multi-passenger booking coordination</li>
 *   <li>Cryptographically secure PNR generation (e.g. {@code PNR-ABC12345})</li>
 *   <li>OpenFeign inter-service calls to Flight Service and Fare Service</li>
 *   <li>Past departure date safeguards and seat capacity validations</li>
 *   <li>Asynchronous event broadcasting via RabbitMQ ({@code BOOKING_CREATED})</li>
 *   <li>Eureka service registry client participation</li>
 * </ul>
 *
 * @author Aerobook Platform Engineering
 * @version 1.0.0
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class BookingServiceApplication {

    /**
     * Bootstraps the Booking Service Spring context on port 8088.
     *
     * @param args command-line bootstrap arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(BookingServiceApplication.class, args);
    }

}
