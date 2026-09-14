package com.aerobook.fare.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.aerobook.fare.dto.response.FlightResponse;

/**
 * ============================================================================
 * Flight Service OpenFeign Declarative HTTP Client
 * ============================================================================
 *
 * Discovers and communicates with the downstream {@code flight-service} via
 * Netflix Eureka service discovery and Spring Cloud OpenFeign.
 *
 * @author Aerobook Platform Engineering
 * @version 1.0.0
 */
@FeignClient(name = "flight-service")
public interface FlightServiceClient {

    /**
     * Resolves flight details and schedule metadata by primary flight ID.
     *
     * @param id the unique flight database identifier
     * @return {@link FlightResponse} containing flight route and timing metadata
     */
    @GetMapping("/api/flights/{id}")
    FlightResponse getFlightById(@PathVariable("id") Long id);

}