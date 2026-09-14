package com.aerobook.flight.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aerobook.flight.dto.request.CreateFlightRequest;
import com.aerobook.flight.dto.request.UpdateFlightRequest;
import com.aerobook.flight.dto.response.FlightResponse;
import com.aerobook.flight.exception.ErrorResponse;
import com.aerobook.flight.service.FlightService;

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
 * Controller for Flight Administration.
 *
 * Microservice Access Policy: - Restricted strictly to ROLE_ADMIN. - Manages
 * flight schedules, timing updates, aircraft assignments, and cancellations.
 */
@Tag(name = "Flight Administration", description = "Administrative flight scheduling, route modifications, and inventory management (Requires ROLE_ADMIN)")
@SecurityRequirement(name = "BearerAuth")
@RestController
@RequestMapping("/api/admin/flights")
public class AdminFlightController {

    private final FlightService flightService;

    /**
     * Constructs a new {@link AdminFlightController} with the flight service.
     *
     * @param flightService flight management service
     */
    public AdminFlightController(FlightService flightService) {
        this.flightService = flightService;
    }

    /**
     * Creates and schedules a new commercial flight linked to an existing
     * aircraft.
     *
     * @param userRoleHeader role header from gateway (must be ROLE_ADMIN)
     * @param request flight scheduling specifications
     * @return 201 CREATED with created {@link FlightResponse}, or 403 FORBIDDEN
     */
    @Operation(
            summary = "Schedule new flight (Admin Only)",
            description = "Creates a new flight schedule linking an existing aircraft fleet ID. Restricted to ROLE_ADMIN."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Flight scheduled successfully",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = FlightResponse.class))),
        @ApiResponse(responseCode = "400", description = "Validation error in request payload",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized: Missing or invalid JWT token"),
        @ApiResponse(responseCode = "403", description = "Forbidden: Insufficient privileges (Requires ROLE_ADMIN)"),
        @ApiResponse(responseCode = "404", description = "Assigned aircraft ID not found",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<?> createFlight(
            @Parameter(hidden = true)
            @RequestHeader(value = "X-User-Role", required = false) String userRoleHeader,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Flight scheduling payload with route, timings, aircraft ID, and base fare",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CreateFlightRequest.class),
                            examples = @ExampleObject(
                                    name = "Scheduled Flight Sample",
                                    summary = "Flight AI101 Mumbai to Delhi",
                                    value = """
                                {
                                  "flightNumber": "AI101",
                                  "airlineName": "Air India",
                                  "source": "Mumbai",
                                  "destination": "Delhi",
                                  "departureTime": "2026-10-01T09:30:00",
                                  "arrivalTime": "2026-10-01T11:45:00",
                                  "totalSeats": 180,
                                  "baseFare": 5500.0,
                                  "aircraftId": 1
                                }
                                """
                            )
                    )
            )
            @Valid @RequestBody CreateFlightRequest request) {

        if (userRoleHeader != null && !userRoleHeader.isBlank() && !"ROLE_ADMIN".equals(userRoleHeader)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied: Scheduling flights requires ROLE_ADMIN authority.");
        }

        FlightResponse response = flightService.createFlight(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Updates an existing flight schedule, route, timings, or pricing.
     *
     * @param id flight database ID to update
     * @param userRoleHeader role header from gateway (must be ROLE_ADMIN)
     * @param request updated flight attributes
     * @return 200 OK with updated {@link FlightResponse}, or 403 FORBIDDEN, or
     * 404 NOT_FOUND
     */
    @Operation(
            summary = "Update flight schedule (Admin Only)",
            description = "Updates details, routes, departure/arrival timings, or seat capacity of an existing flight. Restricted to ROLE_ADMIN."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Flight updated successfully",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = FlightResponse.class))),
        @ApiResponse(responseCode = "400", description = "Validation error in request payload",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized: Missing or invalid JWT token"),
        @ApiResponse(responseCode = "403", description = "Forbidden: Insufficient privileges (Requires ROLE_ADMIN)"),
        @ApiResponse(responseCode = "404", description = "Flight not found with given ID",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> updateFlight(
            @Parameter(name = "id", description = "Flight database ID to update", example = "1", required = true)
            @PathVariable Long id,
            @Parameter(hidden = true)
            @RequestHeader(value = "X-User-Role", required = false) String userRoleHeader,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Updated flight details payload",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UpdateFlightRequest.class),
                            examples = @ExampleObject(
                                    name = "Updated Flight Schedule Sample",
                                    summary = "Updated AI101 Departure & Base Fare",
                                    value = """
                                {
                                  "airlineName": "Air India",
                                  "source": "Mumbai",
                                  "destination": "Delhi",
                                  "departureTime": "2026-10-01T10:00:00",
                                  "arrivalTime": "2026-10-01T12:15:00",
                                  "totalSeats": 180,
                                  "baseFare": 5800.0
                                }
                                """
                            )
                    )
            )
            @Valid @RequestBody UpdateFlightRequest request) {

        if (userRoleHeader != null && !userRoleHeader.isBlank() && !"ROLE_ADMIN".equals(userRoleHeader)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied: Modifying flights requires ROLE_ADMIN authority.");
        }

        FlightResponse response = flightService.updateFlight(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Deletes a flight schedule by database identifier.
     *
     * @param id flight database ID to delete
     * @param userRoleHeader role header from gateway (must be ROLE_ADMIN)
     * @return 200 OK with success confirmation, or 403 FORBIDDEN, or 404
     * NOT_FOUND
     */
    @Operation(
            summary = "Delete flight schedule (Admin Only)",
            description = "Removes a flight from the schedule by database ID. Restricted to ROLE_ADMIN."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Flight deleted successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized: Missing or invalid JWT token"),
        @ApiResponse(responseCode = "403", description = "Forbidden: Insufficient privileges (Requires ROLE_ADMIN)"),
        @ApiResponse(responseCode = "404", description = "Flight not found with given ID",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteFlight(
            @Parameter(name = "id", description = "Flight database ID to delete", example = "1", required = true)
            @PathVariable Long id,
            @Parameter(hidden = true)
            @RequestHeader(value = "X-User-Role", required = false) String userRoleHeader) {

        if (userRoleHeader != null && !userRoleHeader.isBlank() && !"ROLE_ADMIN".equals(userRoleHeader)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied: Deleting flights requires ROLE_ADMIN authority.");
        }

        flightService.deleteFlight(id);
        return ResponseEntity.ok("Flight schedule deleted successfully with ID: " + id);
    }

    /**
     * Administrative lookup of flight details by ID.
     *
     * @param id flight database ID
     * @return 200 OK with {@link FlightResponse}, or 404 NOT_FOUND
     */
    @Operation(
            summary = "Get flight schedule by ID (Admin Only)",
            description = "Fetches admin view of flight schedule details including internal statuses. Restricted to ROLE_ADMIN."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Flight details found",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = FlightResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized: Missing or invalid JWT token"),
        @ApiResponse(responseCode = "403", description = "Forbidden: Insufficient privileges (Requires ROLE_ADMIN)"),
        @ApiResponse(responseCode = "404", description = "Flight not found with given ID",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<FlightResponse> getFlight(
            @Parameter(name = "id", description = "Flight database ID", example = "1", required = true)
            @PathVariable Long id) {

        FlightResponse flight = flightService.getFlightById(id);
        return ResponseEntity.ok(flight);
    }

    /**
     * Returns all scheduled flights across the entire system for administrative
     * review.
     *
     * @return 200 OK with list of all {@link FlightResponse} objects
     */
    @Operation(
            summary = "List all flights (Admin Only)",
            description = "Returns full list of all flight schedules in the system for administrative oversight. Restricted to ROLE_ADMIN."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Flights retrieved successfully",
                content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = FlightResponse.class)))),
        @ApiResponse(responseCode = "401", description = "Unauthorized: Missing or invalid JWT token"),
        @ApiResponse(responseCode = "403", description = "Forbidden: Insufficient privileges (Requires ROLE_ADMIN)")
    })
    @GetMapping
    public ResponseEntity<List<FlightResponse>> getAllFlights() {
        List<FlightResponse> flights = flightService.getAllFlights();
        return ResponseEntity.ok(flights);
    }
}
