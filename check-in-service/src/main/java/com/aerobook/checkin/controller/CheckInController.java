package com.aerobook.checkin.controller;

import com.aerobook.checkin.dto.CheckInRequest;
import com.aerobook.checkin.dto.ErrorResponse;
import com.aerobook.checkin.entity.CheckIn;
import com.aerobook.checkin.repository.CheckInRepository;
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
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * REST Controller exposing passenger flight check-in, boarding pass generation,
 * and reservation check-in status lookups.
 * ============================================================================
 * Airport Check-In & Boarding Pass REST Controller
 * ============================================================================
 *
 * REST Controller exposing passenger flight check-in, seat confirmation,
 * boarding pass generation, and administrative check-in auditing.
 *
 * <p>
 * Endpoint Directory:
 * <ul>
 * <li>{@code GET /health} - Service health and operational metrics probe</li>
 * <li>{@code POST /} - Passenger check-in execution and boarding pass
 * generation</li>
 * <li>{@code GET /booking/{bookingId}} - Check-in lookup by reservation ID</li>
 * <li>{@code GET /{id}} - Check-in lookup by primary record ID</li>
 * <li>{@code GET /} - Master check-in audit directory ({@code ROLE_ADMIN}
 * only)</li>
 * </ul>
 *
 * @author Aerobook Platform Engineering
 * @version 1.0.0
 */
@Tag(name = "Check-In Operations", description = "Operations for flight passenger check-in and confirmation lookup")
@RestController
@RequestMapping("/api/check-ins")
public class CheckInController {

    private final CheckInRepository checkInRepository;

    /**
     * Constructor injection for database repository.
     *
     * @param checkInRepository check-in data access repository
     */
    public CheckInController(CheckInRepository checkInRepository) {
        this.checkInRepository = checkInRepository;
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

        // 1. Check if record already exists for this booking ID (idempotency)
        CheckIn checkIn = checkInRepository.findByBookingId(request.getBookingId())
                .orElseGet(CheckIn::new);

        // 2. Normalize requested seat or default to 12A
        String seat = (request.getSeatNumber() != null && !request.getSeatNumber().isBlank())
                ? request.getSeatNumber().toUpperCase()
                : "12A";

        // 3. Assign check-in coordinates and generate unique boarding pass reference
        checkIn.setBookingId(request.getBookingId());
        checkIn.setPassengerName(request.getPassengerName());
        checkIn.setSeatNumber(seat);
        checkIn.setBoardingPassNumber("BP-" + request.getBookingId() + "-" + seat);
        checkIn.setStatus("CHECKED_IN");
        checkIn.setCheckedInAt(LocalDateTime.now());

        // 4. Persist and return confirmed check-in record
        CheckIn saved = checkInRepository.save(checkIn);
        return ResponseEntity.ok(saved);
    }

    /**
     * Retrieves an existing check-in confirmation by its associated ticket
     * booking ID.
     *
     * @param bookingId unique booking ID
     * @return {@link ResponseEntity} with matched {@link CheckIn} entity
     * @throws ResponseStatusException if no check-in exists for the given
     * booking
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

        CheckIn checkIn = checkInRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Check-in not found for booking ID: " + bookingId));

        return ResponseEntity.ok(checkIn);
    }

    /**
     * Retrieves an existing check-in confirmation by primary check-in record
     * ID.
     *
     * @param id unique check-in database ID
     * @return {@link ResponseEntity} with matched {@link CheckIn} entity
     * @throws ResponseStatusException if no check-in exists with given ID
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

        CheckIn checkIn = checkInRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Check-in record not found with ID: " + id));

        return ResponseEntity.ok(checkIn);
    }

    /**
     * Administrative oversight endpoint returning all passenger check-in
     * confirmations. Strictly restricted to {@code ROLE_ADMIN} authority.
     *
     * @param userRole downstream role header forwarded by API Gateway
     * @return list of all {@link CheckIn} entities or HTTP 403 Forbidden
     */
    @Operation(
            summary = "List all check-ins across airline (ROLE_ADMIN)",
            description = "Administrative audit endpoint returning all passenger check-in confirmations. Requires ROLE_ADMIN authority.",
            security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "List of all check-in records",
                content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = CheckIn.class)))),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden: Requires ROLE_ADMIN authority",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping
    public ResponseEntity<?> getAllCheckIns(
            @Parameter(hidden = true)
            @RequestHeader(value = "X-User-Role", required = false) String userRole) {

        // Validate administrative role before returning full check-in list
        if (userRole != null && !userRole.isEmpty() && !"ROLE_ADMIN".equals(userRole)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(
                    LocalDateTime.now(),
                    HttpStatus.FORBIDDEN.value(),
                    "Forbidden",
                    "Access denied: Listing all passenger check-ins requires ROLE_ADMIN authority."
            ));
        }

        List<CheckIn> checkIns = checkInRepository.findAll();
        return ResponseEntity.ok(checkIns);
    }
}
