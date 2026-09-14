package com.aerobook.flight.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ AMQP messaging configuration for Flight Service. Declares queues for
 * asynchronous flight notifications and booking lifecycle event listening.
 */
@Configuration
public class RabbitMQConfig {

    /**
     * RabbitMQ queue name for outgoing flight events (e.g., flight creation,
     * updates).
     */
    public static final String FLIGHT_QUEUE = "flight.queue";

    /**
     * RabbitMQ queue name for incoming booking creation events consumed to
     * adjust seat counts.
     */
    public static final String BOOKING_CREATED_QUEUE = "booking-created";

    /**
     * Declares the durable flight event queue.
     *
     * @return the flight AMQP queue
     */
    @Bean
    public Queue flightQueue() {
        return new Queue(FLIGHT_QUEUE);
    }

    /**
     * Declares the durable booking creation event queue.
     *
     * @return the booking-created AMQP queue
     */
    @Bean
    public Queue bookingCreatedQueue() {
        return new Queue(BOOKING_CREATED_QUEUE);
    }
}
