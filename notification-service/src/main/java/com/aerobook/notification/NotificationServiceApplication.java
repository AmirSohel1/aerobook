package com.aerobook.notification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * ============================================================================
 * AeroBook Real-Time Notification & Operational Alerts Microservice (Port 8091)
 * ============================================================================
 *
 * Microservice handling passenger flight notices, gate changes, operational
 * delay alerts, check-in confirmations, and broadcast communications.
 *
 * @author Aerobook Platform Engineering
 * @version 1.0.0
 */
@SpringBootApplication
@EnableDiscoveryClient
public class NotificationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(NotificationServiceApplication.class, args);
    }
}
