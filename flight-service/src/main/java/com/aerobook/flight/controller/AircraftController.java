package com.aerobook.flight.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aerobook.flight.dto.request.CreateAircraftRequest;
import com.aerobook.flight.entity.Aircraft;
import com.aerobook.flight.exception.ErrorResponse;
import com.aerobook.flight.service.AircraftService;

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
 * REST Controller exposing administrative fleet inventory and aircraft model
 * management.
 *
 * Microservice Access Policy: - Restricted strictly to ROLE_ADMIN. - Controls
 * aircraft models, registration identifiers, and physical seating capacities.
 */
@Tag(name = "Aircraft Fleet Management", description = "Operations for managing aircraft fleet models, registration, and seating capacity (Requires ROLE_ADMIN)")
@SecurityRequirement(name = "BearerAuth")
@RestController
@RequestMapping("/api/admin/aircrafts")
public class AircraftController {

    private final AircraftService aircraftService;

    /**
     * Constructs a new {@link AircraftController} with the aircraft service.
     *
     * @param aircraftService the aircraft service bean
     */
    public AircraftController(AircraftService aircraftService) {
        this.aircraftService = aircraftService;
    }

    /**
     * Registers a new aircraft model and seating capacity in the fleet
     * database.
     *
     * @param userRoleHeader role header passed from gateway (must be
     * ROLE_ADMIN)
     * @param request specifications of the aircraft to register
     * @return 201 CREATED with the saved {@link Aircraft} entity, or 403
     * FORBIDDEN
     */
    @Operation(
            summary = "Register new aircraft (Admin Only)",
            description = "Adds a new aircraft to the commercial fleet inventory. Restricted to ROLE_ADMIN."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Aircraft registered successfully",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Aircraft.class))),
        @ApiResponse(responseCode = "400", description = "Validation error in aircraft payload",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized: Missing or invalid JWT token"),
        @ApiResponse(responseCode = "403", description = "Forbidden: Insufficient privileges (Requires ROLE_ADMIN)")
    })
    @PostMapping
    public ResponseEntity<?> createAircraft(
            @Parameter(hidden = true)
            @RequestHeader(value = "X-User-Role", required = false) String userRoleHeader,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Aircraft registration specifications payload",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CreateAircraftRequest.class),
                            examples = @ExampleObject(
                                    name = "Airbus A320 Sample",
                                    summary = "Standard Narrow-body Fleet Aircraft",
                                    value = """
                                {
                                  "aircraftCode": "A320-001",
                                  "aircraftName": "Airbus A320",
                                  "manufacturer": "Airbus",
                                  "capacity": 180
                                }
                                """
                            )
                    )
            )
            @Valid @RequestBody CreateAircraftRequest request) {

        if (userRoleHeader != null && !userRoleHeader.isBlank() && !"ROLE_ADMIN".equals(userRoleHeader)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied: Registering aircraft requires ROLE_ADMIN authority.");
        }

        Aircraft aircraft = aircraftService.createAircraft(request);
        return new ResponseEntity<>(aircraft, HttpStatus.CREATED);
    }

    /**
     * Retrieves aircraft specifications and capacity by database ID.
     *
     * @param id the primary key of the aircraft
     * @return 200 OK with {@link Aircraft} entity, or 404 NOT_FOUND
     */
    @Operation(
            summary = "Get aircraft by ID (Admin Only)",
            description = "Retrieves aircraft specifications, model codes, and physical capacity by ID. Restricted to ROLE_ADMIN."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Aircraft details found",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Aircraft.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized: Missing or invalid JWT token"),
        @ApiResponse(responseCode = "403", description = "Forbidden: Insufficient privileges (Requires ROLE_ADMIN)"),
        @ApiResponse(responseCode = "404", description = "Aircraft not found with given ID",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<Aircraft> getAircraft(
            @Parameter(name = "id", description = "Aircraft database ID", example = "1", required = true)
            @PathVariable Long id) {

        Aircraft aircraft = aircraftService.getAircraft(id);
        return ResponseEntity.ok(aircraft);
    }

    /**
     * Returns full fleet roster of all aircraft registered in the system.
     *
     * @return 200 OK with list of all {@link Aircraft} entities
     */
    @Operation(
            summary = "List all aircraft (Admin Only)",
            description = "Returns complete fleet list of all aircraft registered in the inventory. Restricted to ROLE_ADMIN."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Aircraft list retrieved successfully",
                content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Aircraft.class)))),
        @ApiResponse(responseCode = "401", description = "Unauthorized: Missing or invalid JWT token"),
        @ApiResponse(responseCode = "403", description = "Forbidden: Insufficient privileges (Requires ROLE_ADMIN)")
    })
    @GetMapping
    public ResponseEntity<List<Aircraft>> getAllAircrafts() {
        List<Aircraft> aircrafts = aircraftService.getAllAircrafts();
        return ResponseEntity.ok(aircrafts);
    }

    /**
     * Removes an aircraft from the fleet inventory by database ID.
     *
     * @param id database ID of the aircraft to remove
     * @param userRoleHeader role header from gateway (must be ROLE_ADMIN)
     * @return 200 OK with success message, or 403 FORBIDDEN, or 404 NOT_FOUND
     */
    @Operation(
            summary = "Delete aircraft (Admin Only)",
            description = "Permanently removes an aircraft from the fleet inventory by ID. Restricted to ROLE_ADMIN."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Aircraft deleted successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized: Missing or invalid JWT token"),
        @ApiResponse(responseCode = "403", description = "Forbidden: Insufficient privileges (Requires ROLE_ADMIN)"),
        @ApiResponse(responseCode = "404", description = "Aircraft not found with given ID",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAircraft(
            @Parameter(name = "id", description = "Aircraft database ID to delete", example = "1", required = true)
            @PathVariable Long id,
            @Parameter(hidden = true)
            @RequestHeader(value = "X-User-Role", required = false) String userRoleHeader) {

        if (userRoleHeader != null && !userRoleHeader.isBlank() && !"ROLE_ADMIN".equals(userRoleHeader)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied: Deleting aircraft requires ROLE_ADMIN authority.");
        }

        aircraftService.deleteAircraft(id);
        return ResponseEntity.ok("Aircraft deleted successfully with ID: " + id);
    }
}
