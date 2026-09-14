package com.aerobook.flight.producer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * Producer class responsible for publishing flight-related events to RabbitMQ.
 */
@Component
public class FlightEventProducer {

    private static final Logger log = LoggerFactory.getLogger(FlightEventProducer.class);

    private final RabbitTemplate rabbitTemplate;

    /**
     * Constructor Injection.
     *
     * @param rabbitTemplate RabbitTemplate instance
     */
    public FlightEventProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * Publish event to RabbitMQ queue.
     *
     * @param queue Queue name
     * @param event Event payload
     */
    public void publish(String queue, Object event) {

        rabbitTemplate.convertAndSend(
                queue,
                event);

        log.info("Dispatched flight event to RabbitMQ queue '{}': {}", queue, event);
    }
}
