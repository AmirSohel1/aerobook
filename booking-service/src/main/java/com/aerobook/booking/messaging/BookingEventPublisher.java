package com.aerobook.booking.messaging;

import com.aerobook.booking.config.RabbitMqConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Publishes booking lifecycle events without coupling consumers to JPA.
 * Publishes booking lifecycle events to RabbitMQ queues without coupling
 * downstream consumers to JPA. Emits JSON-formatted message events on
 * reservation milestones (such as booking confirmation).
 */
@Component
public class BookingEventPublisher {

    /**
     * RabbitTemplate used for AMQP message sending.
     */
    private final RabbitTemplate rabbitTemplate;

    /**
     * Feature flag controlling whether RabbitMQ message dispatch is enabled.
     */
    @Value("${app.rabbitmq.enabled:false}")
    private boolean enabled;

    /**
     * Constructs a new {@link BookingEventPublisher} with the given
     * {@link RabbitTemplate}.
     *
     * @param rabbitTemplate the Spring AMQP rabbit template
     */
    public BookingEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * Publishes a booking created event containing the assigned PNR. If
     * RabbitMQ messaging is disabled in configuration, this operation safely
     * no-ops.
     *
     * @param pnr the Passenger Name Record of the confirmed booking
     */
    public void publishCreated(String pnr) {
        if (!enabled) {
            return;
        }
        rabbitTemplate.convertAndSend(
                RabbitMqConfig.BOOKING_CREATED_QUEUE,
                "BOOKING_CREATED:" + pnr);
    }
}
