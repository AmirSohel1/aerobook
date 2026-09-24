package com.aerobook.checkin.controller;

import com.aerobook.checkin.dto.CheckInRequest;
import com.aerobook.checkin.dto.ErrorResponse;
import com.aerobook.checkin.entity.CheckIn;
import com.aerobook.checkin.service.CheckInService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * ============================================================================
 * Airport Check-In & Boarding Pass REST Controller
 * ============================================================================
 *
 * REST Controller exposing passenger flight check-in, seat confirmation,
 * boarding pass generation, departure gate verifications, and administrative
 * auditing.
 *
 * <p>
 * Endpoint Directory:
 * <ul>
 * <li>{@code GET /health} - Service health and operational metrics probe</li>
 * <li>{@code POST /} - Passenger check-in execution and boarding pass
 * generation</li>
 * <li>{@code GET /booking/{bookingId}} - Check-in lookup by reservation ID</li>
 * <li>{@code GET /{id}} - Check-in lookup by primary record ID</li>
 * <li>{@code GET /} - Master check-in audit directory ({@code ROLE_ADMIN} or
 * {@code ROLE_STAFF})</li>
 * <li>{@code POST /verify} - Departure gate boarding pass scanner
 * ({@code ROLE_ADMIN} or {@code ROLE_STAFF})</li>
 * </ul>
 *
 * @author Aerobook Platform Engineering
 * @version 1.0.0
 */
@Tag(name = "Check-In Operations", description = "Operations for flight passenger check-in, boarding pass verification, and confirmation lookup")
@RestController
@RequestMapping("/api/check-ins")
public class CheckInController {

    private final CheckInService checkInService;

    /**
     * Constructor injection for check-in business service.
     *
     * @param checkInService check-in business service
     */
    public CheckInController(CheckInService checkInService) {
        this.checkInService = checkInService;
    }

    /**
     * Operational health probe for API Gateway routing checks and orchestrator
     * liveness.
     *
     * @return {@link ResponseEntity} containing service status map
     */
    @Operation(summary = "Check-In Service Health Probe", description = "Liveness probe for API Gateway and orchestrators.")
    @ApiResponse(responseCode = "200", description = "Check-In Service is operational")
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> checkHealth() {
        return ResponseEntity.ok(Map.of(
                "service", "check-in-service",
                "status", "UP",
                "port", 8090,
                "timestamp", LocalDateTime.now().toString()
        ));
    }

    /**
     * Checks in a passenger for a confirmed booking, assigns seat, and
     * generates boarding pass. Idempotent: re-checking in the same booking ID
     * updates the existing confirmation.
     *
     * @param request validated check-in coordinates including booking ID and
     * passenger name
     * @return {@link ResponseEntity} with confirmed {@link CheckIn} entity
     */
    @Operation(
            summary = "Check in passenger & generate boarding pass",
            description = "Performs check-in for a passenger on a confirmed booking, assigns a seat number, "
            + "generates a unique boarding pass identifier, and records status as CHECKED_IN.",
            security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Check-in successful, boarding pass issued",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = CheckIn.class))),
        @ApiResponse(responseCode = "400", description = "Invalid booking or passenger parameters",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized: Missing or invalid JWT")
    })
    @PostMapping
    public ResponseEntity<CheckIn> checkIn(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Passenger check-in and seat assignment request",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CheckInRequest.class),
                            examples = @ExampleObject(
                                    name = "Asha Khan Check-In",
                                    summary = "Pre-populated test check-in for Booking ID 1",
                                    value = """
                                            {
                                              "bookingId": 1,
                                              "passengerName": "Asha Khan",
                                              "seatNumber": "12A"
                                            }
                                            """
                            )
                    )
            )
            @Valid @RequestBody CheckInRequest request) {

        CheckIn saved = checkInService.performCheckIn(request);
        return ResponseEntity.ok(saved);
    }

    /**
     * Retrieves an existing check-in confirmation by its associated ticket
     * booking ID.
     *
     * @param bookingId unique booking ID
     * @return {@link ResponseEntity} with matched {@link CheckIn} entity
     */
    @Operation(
            summary = "Get check-in record by booking ID",
            description = "Retrieves existing check-in details, assigned seat, and boarding pass for a booking ID.",
            security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Check-in record found",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = CheckIn.class))),
        @ApiResponse(responseCode = "404", description = "Check-in not found for specified booking",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<CheckIn> getByBooking(
            @Parameter(name = "bookingId", description = "Booking database ID", example = "1")
            @PathVariable Long bookingId) {

        return ResponseEntity.ok(checkInService.getCheckInByBookingId(bookingId));
    }

    /**
     * Retrieves an existing check-in confirmation by primary check-in record
     * ID.
     *
     * @param id unique check-in database ID
     * @return {@link ResponseEntity} with matched {@link CheckIn} entity
     */
    @Operation(
            summary = "Get check-in record by ID",
            description = "Retrieves existing check-in confirmation by primary check-in ID.",
            security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Check-in record found",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = CheckIn.class))),
        @ApiResponse(responseCode = "404", description = "Check-in not found with specified ID",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/{id}")
    public ResponseEntity<CheckIn> getById(
            @Parameter(name = "id", description = "Check-in database ID", example = "1")
            @PathVariable Long id) {

        return ResponseEntity.ok(checkInService.getCheckInById(id));
    }

    /**
     * Administrative and staff oversight endpoint returning all passenger
     * check-in confirmations. Restricted to {@code ROLE_ADMIN} or
     * {@code ROLE_STAFF} authority.
     *
     * @param userRole downstream role header forwarded by API Gateway
     * @return list of all {@link CheckIn} entities or HTTP 403 Forbidden
     */
    @Operation(
            summary = "List all check-ins across airline (ROLE_ADMIN & ROLE_STAFF)",
            description = "Administrative audit endpoint returning all passenger check-in confirmations. Requires ROLE_ADMIN or ROLE_STAFF authority.",
            security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "List of all check-in records",
                content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = CheckIn.class)))),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden: Requires ROLE_ADMIN or ROLE_STAFF authority",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping
    public ResponseEntity<?> getAllCheckIns(
            @Parameter(hidden = true)
            @RequestHeader(value = "X-User-Role", required = false) String userRole) {

        // Validate administrative or staff role before returning full check-in list
        if (userRole != null && !userRole.isEmpty() && !"ROLE_ADMIN".equals(userRole) && !"ROLE_STAFF".equals(userRole)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(
                    LocalDateTime.now(),
                    HttpStatus.FORBIDDEN.value(),
                    "Forbidden",
                    "Access denied: Listing passenger check-ins requires ROLE_ADMIN or ROLE_STAFF authority."
            ));
        }

        List<CheckIn> checkIns = checkInService.getAllCheckIns();
        return ResponseEntity.ok(checkIns);
    }

    /**
     * Verifies a passenger's boarding pass at the departure gate and updates
     * status to BOARDED. Accessible by Airport Operations Staff (ROLE_STAFF)
     * and Admins (ROLE_ADMIN).
     *
     * @param boardingPassNumber unique boarding pass identifier (e.g. BP-1-12A)
     * @param userRole role header propagated by API Gateway
     * @return 200 OK with confirmed and boarded {@link CheckIn} entity
     */
    @Operation(
            summary = "Verify boarding pass and mark as BOARDED (Staff & Admin)",
            description = "Airport gate verification endpoint. Validates boarding pass reference and marks passenger as BOARDED.",
            security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Boarding pass verified and passenger marked as BOARDED",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = CheckIn.class))),
        @ApiResponse(responseCode = "404", description = "Boarding pass not found",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "403", description = "Forbidden: Requires ROLE_STAFF or ROLE_ADMIN")
    })
    @PostMapping("/verify")
    public ResponseEntity<?> verifyBoardingPass(
            @Parameter(name = "boardingPassNumber", description = "Boarding pass code (e.g. BP-1-12A)", required = true)
            @RequestParam String boardingPassNumber,
            @Parameter(hidden = true)
            @RequestHeader(value = "X-User-Role", required = false) String userRole) {

        if (userRole != null && !userRole.isEmpty() && !"ROLE_ADMIN".equals(userRole) && !"ROLE_STAFF".equals(userRole)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(
                    LocalDateTime.now(),
                    HttpStatus.FORBIDDEN.value(),
                    "Forbidden",
                    "Access denied: Verifying boarding passes requires ROLE_STAFF or ROLE_ADMIN privileges."
            ));
        }

        CheckIn checkIn = checkInService.verifyBoardingPass(boardingPassNumber);
        return ResponseEntity.ok(checkIn);
    }
}
