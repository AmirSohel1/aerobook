package com.aerobook.fare.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.aerobook.fare.dto.request.FareRequest;
import com.aerobook.fare.dto.response.ErrorResponse;
import com.aerobook.fare.dto.response.FareResponse;
import com.aerobook.fare.service.FareService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * ============================================================================
 * Fare & Revenue Management REST Controller
 * ============================================================================
 *
 * REST Controller exposing flight fare configuration endpoints, tiered pricing
 * breakdowns, tax calculations, and promotional discount updates under {@code /api/fares}.
 *
 * <p>Access Policy Summary:
 * <ul>
 *   <li>{@code GET /health} - Public operational probe</li>
 *   <li>{@code GET /{id}}, {@code GET /flight/{flightId}}, {@code GET /} - All authenticated users</li>
 *   <li>{@code POST /}, {@code PUT /{id}}, {@code DELETE /{id}} - Restricted strictly to {@code ROLE_ADMIN}</li>
 * </ul>
 *
 * @author Aerobook Platform Engineering
 * @version 1.0.0
 */
@Tag(name = "Fare Management", description = "Operations for flight pricing tiers, discounts, and tax rates")
@RestController
@RequestMapping("/api/fares")
public class FareController {

    private final FareService fareService;

    /**
     * Constructor injection for fare business service.
     *
     * @param fareService fare business service
     */
    public FareController(FareService fareService) {
        this.fareService = fareService;
    }

    /**
     * Health check liveness probe for API Gateway and platform monitors.
     *
     * @return {@link ResponseEntity} with health confirmation map
     */
    @Operation(summary = "Fare Service Health Probe", description = "Liveness probe for API Gateway and health monitors.")
    @ApiResponse(responseCode = "200", description = "Fare Service is operational")
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> checkHealth() {
        return ResponseEntity.ok(Map.of(
                "service", "fare-service",
                "status", "UP",
                "port", 8089,
                "timestamp", LocalDateTime.now().toString()
        ));
    }

    /**
     * Configures tiered cabin pricing, taxes, and discounts for a commercial flight.
     * Restricted to {@code ROLE_ADMIN}.
     *
     * @param userRole downstream role header forwarded by API Gateway
     * @param request validated fare creation payload
     * @return {@link ResponseEntity} containing created {@link FareResponse} with HTTP 201
     */
    @Operation(
            summary = "Create flight fare pricing (Admin Only)",
            description = "Configures tiered pricing (economy, business, first class), taxes, and discounts for a flight. Restricted to ROLE_ADMIN.",
            security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Fare created successfully",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = FareResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid fare payload",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized: Missing or invalid JWT"),
        @ApiResponse(responseCode = "403", description = "Forbidden: Requires ROLE_ADMIN authority")
    })
    @PostMapping
    public ResponseEntity<?> createFare(
            @Parameter(hidden = true)
            @RequestHeader(value = "X-User-Role", required = false) String userRole,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Fare pricing configuration payload",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = FareRequest.class),
                            examples = @ExampleObject(
                                    name = "Flight AI101 Tiered Pricing",
                                    summary = "Sample tiered pricing for Flight ID 1",
                                    value = """
                                            {
                                              "flightId": 1,
                                              "economyFare": 4500.00,
                                              "businessFare": 8500.00,
                                              "firstClassFare": 14000.00,
                                              "taxPercentage": 18.0,
                                              "discountPercentage": 5.0,
                                              "effectiveDate": "2026-10-01"
                                            }
                                            """
                            )
                    )
            )
            @Valid @RequestBody FareRequest request) {

        // Validate administrative privileges
        if (userRole != null && !userRole.isEmpty() && !"ROLE_ADMIN".equals(userRole)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(
                    LocalDateTime.now(),
                    HttpStatus.FORBIDDEN.value(),
                    "Forbidden",
                    "Access denied: Creating flight fares requires ROLE_ADMIN privileges."
            ));
        }

        FareResponse response = fareService.createFare(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Retrieves pricing breakdown by primary fare database ID.
     *
     * @param id fare record database ID
     * @return {@link ResponseEntity} with matched {@link FareResponse}
     */
    @Operation(
            summary = "Get fare by ID",
            description = "Retrieves pricing breakdown for a specific fare database ID.",
            security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Fare details retrieved",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = FareResponse.class))),
        @ApiResponse(responseCode = "404", description = "Fare not found",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/{id}")
    public ResponseEntity<FareResponse> getFareById(
            @Parameter(name = "id", description = "Fare database ID", example = "1")
            @PathVariable Long id) {

        return ResponseEntity.ok(fareService.getFareById(id));
    }

    /**
     * Retrieves active fare pricing for a specific flight schedule.
     *
     * @param flightId flight database identifier
     * @return {@link ResponseEntity} with matched {@link FareResponse}
     */
    @Operation(
            summary = "Get fare by Flight ID",
            description = "Retrieves active fare configuration for a specific scheduled flight ID.",
            security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Fare details for flight",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = FareResponse.class))),
        @ApiResponse(responseCode = "404", description = "No fare found for flight",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/flight/{flightId}")
    public ResponseEntity<FareResponse> getFareByFlightId(
            @Parameter(name = "flightId", description = "Flight database ID", example = "1")
            @PathVariable Long flightId) {

        return ResponseEntity.ok(fareService.getFareByFlightId(flightId));
    }

    /**
     * Lists all flight fare configurations registered in the system.
     *
     * @return {@link ResponseEntity} containing list of {@link FareResponse} objects
     */
    @Operation(
            summary = "List all configured fares",
            description = "Returns full list of all flight fares in the system.",
            security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "List of fares retrieved successfully",
                content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = FareResponse.class)))),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping
    public ResponseEntity<List<FareResponse>> getAllFares() {
        return ResponseEntity.ok(fareService.getAllFares());
    }

    /**
     * Updates prices, discounts, or tax percentages for an existing fare record.
     * Restricted to {@code ROLE_ADMIN}.
     *
     * @param id fare record database ID
     * @param userRole downstream role header forwarded by API Gateway
     * @param request validated updated pricing payload
     * @return {@link ResponseEntity} with updated {@link FareResponse} or HTTP 403 Forbidden
     */
    @Operation(
            summary = "Update fare pricing (Admin Only)",
            description = "Updates prices, discounts, or tax percentages for an existing fare ID. Restricted to ROLE_ADMIN.",
            security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Fare updated successfully",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = FareResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid fare update payload",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Fare not found",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden: Requires ROLE_ADMIN")
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> updateFare(
            @Parameter(name = "id", description = "Fare database ID to update", example = "1")
            @PathVariable Long id,
            @Parameter(hidden = true)
            @RequestHeader(value = "X-User-Role", required = false) String userRole,
            @Valid @RequestBody FareRequest request) {

        // Validate administrative role before allowing fare update
        if (userRole != null && !userRole.isEmpty() && !"ROLE_ADMIN".equals(userRole)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(
                    LocalDateTime.now(),
                    HttpStatus.FORBIDDEN.value(),
                    "Forbidden",
                    "Access denied: Modifying flight fares requires ROLE_ADMIN privileges."
            ));
        }

        return ResponseEntity.ok(fareService.updateFare(id, request));
    }

    /**
     * Permanently deletes a fare pricing structure. Restricted to {@code ROLE_ADMIN}.
     *
     * @param id fare record database ID
     * @param userRole downstream role header forwarded by API Gateway
     * @return confirmation message or HTTP 403 Forbidden
     */
    @Operation(
            summary = "Delete fare (Admin Only)",
            description = "Permanently removes a fare record by ID. Restricted to ROLE_ADMIN.",
            security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Fare deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Fare not found",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden: Requires ROLE_ADMIN")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFare(
            @Parameter(name = "id", description = "Fare database ID to delete", example = "1")
            @PathVariable Long id,
            @Parameter(hidden = true)
            @RequestHeader(value = "X-User-Role", required = false) String userRole) {

        // Validate administrative role before allowing deletion
        if (userRole != null && !userRole.isEmpty() && !"ROLE_ADMIN".equals(userRole)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(
                    LocalDateTime.now(),
                    HttpStatus.FORBIDDEN.value(),
                    "Forbidden",
                    "Access denied: Deleting flight fares requires ROLE_ADMIN privileges."
            ));
        }

        fareService.deleteFare(id);
        return ResponseEntity.ok("Fare deleted successfully with ID: " + id);
    }
}
