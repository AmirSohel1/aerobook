package com.aerobook.apigateway.controller;

import com.aerobook.apigateway.config.GatewayOpenApiPathsBuilder;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ============================================================================
 * Gateway OpenAPI Definition Controller (Multi-Service Dropdown Engine)
 * ============================================================================
 *
 * REST Controller serving isolated, per-microservice OpenAPI 3 specifications
 * consumed by the Swagger UI definition selector dropdown.
 *
 * <p>
 * Key Architecture Benefits:
 * <ul>
 * <li><b>Zero Vertical Scrolling:</b> Dynamically returns OpenAPI models scoped
 * strictly to the selected microservice (e.g. Auth, Flight, Booking).</li>
 * <li><b>Instant Local Availability:</b> Specs are generated and served
 * directly by the Gateway, eliminating network hops and downstream service
 * dependency during UI rendering.</li>
 * <li><b>Hidden Internal Endpoint:</b> Annotated with {@link Hidden} to keep
 * internal doc-serving endpoints out of user-facing endpoint catalogs.</li>
 * </ul>
 *
 * @author Aerobook Platform Engineering
 * @version 1.0.0
 */
@Hidden
@RestController
@RequestMapping("/api/gateway/docs")
public class GatewayDocsController {

    /**
     * Retrieves an isolated OpenAPI 3 JSON specification for a specific
     * microservice.
     *
     * <p>
     * Supported service identifiers:
     * <ul>
     * <li>{@code auth-service} / {@code auth}: Authentication & Security
     * Authority</li>
     * <li>{@code user-service} / {@code user}: User Profile Management</li>
     * <li>{@code flight-service} / {@code flight}: Flight Scheduling &
     * Fleet</li>
     * <li>{@code fare-service} / {@code fare}: Fare & Revenue Management</li>
     * <li>{@code booking-service} / {@code booking}: Flight Booking &
     * Messaging</li>
     * <li>{@code check-in-service} / {@code check-in}: Airport Check-In &
     * Boarding Pass</li>
     * <li>{@code gateway}: Central API Gateway Hub diagnostics</li>
     * <li>{@code all} or any unknown key: Complete aggregated platform
     * view</li>
     * </ul>
     *
     * @param serviceId the case-insensitive microservice identifier
     * @return {@link ResponseEntity} containing the service-scoped
     * {@link OpenAPI} specification
     */
    @GetMapping(value = "/{serviceId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getServiceOpenApi(@PathVariable String serviceId) {
        OpenAPI openApi = GatewayOpenApiPathsBuilder.buildServiceOpenAPI(serviceId);
        try {
            String json = io.swagger.v3.core.util.Json.mapper().writeValueAsString(openApi);
            return ResponseEntity.ok(json);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("{}");
        }
    }

    /**
     * Helper accessor returning the parsed {@link OpenAPI} domain model for
     * unit testing and programmatic introspection.
     *
     * @param serviceId the service identifier
     * @return populated {@link OpenAPI} instance
     */
    public OpenAPI getServiceOpenApiModel(String serviceId) {
        return GatewayOpenApiPathsBuilder.buildServiceOpenAPI(serviceId);
    }
}
