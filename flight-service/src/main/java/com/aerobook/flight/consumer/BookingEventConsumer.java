package com.aerobook.flight.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Asynchronous consumer listening to RabbitMQ queues for booking lifecycle
 * events. Triggered on booking creation to synchronize seat counts and
 * inventory.
 */
@Component
@ConditionalOnProperty(
        name = "app.rabbitmq.enabled",
        havingValue = "true",
        matchIfMissing = false)
public class BookingEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(BookingEventConsumer.class);

    /**
     * Receives booking creation notification messages from the
     * "booking-created" queue.
     *
     * @param message payload containing PNR and booking confirmation metadata
     */
    @RabbitListener(queues = "booking-created")
    public void receive(String message) {

        log.info("Received booking event from queue 'booking-created': {}", message);

    }
}
