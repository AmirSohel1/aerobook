package com.aerobook.booking.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ============================================================================
 * RabbitMQ Messaging Infrastructure Configuration
 * ============================================================================
 *
 * Configures the durable AMQP queue used for publishing asynchronous
 * {@code BOOKING_CREATED} events when a passenger ticket is confirmed.
 *
 * @author Aerobook Platform Engineering
 * @version 1.0.0
 */
@Configuration
public class RabbitMqConfig {

    /**
     * Standard destination queue name consumed by Flight Service for seat synchronization.
     */
    public static final String BOOKING_CREATED_QUEUE = "booking-created";

    /**
     * Declares a durable AMQP queue ensuring messages survive RabbitMQ broker restarts.
     *
     * @return configured durable {@link Queue}
     */
    @Bean
    public Queue bookingCreatedQueue() {
        return new Queue(BOOKING_CREATED_QUEUE, true);
    }
}
