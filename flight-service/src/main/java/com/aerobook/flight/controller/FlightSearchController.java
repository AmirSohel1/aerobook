package com.aerobook.flight.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aerobook.flight.dto.response.FlightResponse;
import com.aerobook.flight.exception.ErrorResponse;
import com.aerobook.flight.service.FlightSearchService;
import com.aerobook.flight.service.FlightService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * REST Controller exposing customer-facing and inter-service flight discovery
 * APIs.
 *
 * Microservice Access Policy: - Public: Health check (/health) - Customer &
 * Admin: Search flights by route, view flight details by ID, browse scheduled
 * flights catalog
 */
@Tag(name = "Flight Search & Catalog", description = "Customer operations for searching, viewing schedules, and browsing flights")
@SecurityRequirement(name = "BearerAuth")
@RestController
@RequestMapping("/api/flights")
public class FlightSearchController {

    private final FlightSearchService flightSearchService;
    private final FlightService flightService;

    /**
     * Constructs a new {@link FlightSearchController} with required services.
     *
     * @param flightSearchService flight route search service
     * @param flightService flight CRUD and catalog service
     */
    public FlightSearchController(
            FlightSearchService flightSearchService,
            FlightService flightService) {

        this.flightSearchService = flightSearchService;
        this.flightService = flightService;
    }

    /**
     * Operational health check for Flight Service.
     *
     * @return 200 OK string verifying service readiness
     */
    @Operation(summary = "Service health check", description = "Verifies operational readiness of Flight Service. Public endpoint.", security = {})
    @ApiResponse(responseCode = "200", description = "Flight Service is healthy and operational")
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Flight Service is up and running!");
    }

    /**
     * Search flights by source departure and destination arrival cities.
     *
     * @param source departure city or IATA code
     * @param destination arrival city or IATA code
     * @return 200 OK list of matching {@link FlightResponse}
     */
    @Operation(
            summary = "Search flights by route",
            description = "Searches for active scheduled flights matching the departure origin and arrival destination cities."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "List of matching scheduled flights",
                content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = FlightResponse.class)))),
        @ApiResponse(responseCode = "401", description = "Unauthorized: Missing or invalid JWT token")
    })
    @GetMapping("/search")
    public ResponseEntity<List<FlightResponse>> searchFlights(
            @Parameter(name = "source", description = "Origin / Departure city", example = "Mumbai", required = true)
            @RequestParam String source,
            @Parameter(name = "destination", description = "Destination / Arrival city", example = "Delhi", required = true)
            @RequestParam String destination) {

        List<FlightResponse> flights = flightSearchService.searchFlights(source, destination);
        return ResponseEntity.ok(flights);
    }

    /**
     * Returns complete scheduled flights catalog. Accessible by customers and
     * admins.
     *
     * @return 200 OK list of all {@link FlightResponse}
     */
    @Operation(
            summary = "Browse all scheduled flights",
            description = "Returns complete catalog of all active scheduled flights in the airline system."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "List of all flights retrieved successfully",
                content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = FlightResponse.class)))),
        @ApiResponse(responseCode = "401", description = "Unauthorized: Missing or invalid JWT token")
    })
    @GetMapping
    public ResponseEntity<List<FlightResponse>> getAllFlights() {
        List<FlightResponse> flights = flightService.getAllFlights();
        return ResponseEntity.ok(flights);
    }

    /**
     * Provides flight lookup by ID for customer inquiry and inter-service Feign
     * communication (Booking Service).
     *
     * @param id the flight database ID
     * @return 200 OK with {@link FlightResponse} or 404 NOT_FOUND
     */
    @Operation(
            summary = "Get flight details by ID",
            description = "Retrieves complete flight schedule, timings, seat availability, and base fare by ID. "
            + "Used by customers and internally by Booking Service OpenFeign client."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Flight details found",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = FlightResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized: Missing or invalid JWT token"),
        @ApiResponse(responseCode = "404", description = "Flight not found with given ID",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<FlightResponse> getFlightById(
            @Parameter(name = "id", description = "Flight database identifier", example = "1", required = true)
            @PathVariable Long id) {

        FlightResponse flight = flightService.getFlightById(id);
        return ResponseEntity.ok(flight);
    }
}
