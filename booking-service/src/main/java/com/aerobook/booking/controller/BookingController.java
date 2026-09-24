package com.aerobook.booking.controller;

import com.aerobook.booking.dto.BookingRequest;
import com.aerobook.booking.dto.BookingResponse;
import com.aerobook.booking.dto.ErrorResponse;
import com.aerobook.booking.service.BookingService;
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
 * Flight Booking REST Controller
 * ============================================================================
 *
 * REST Controller exposing flight reservation, PNR lookups, user bookings,
 * flight passenger manifest retrieval, admin booking oversight, and
 * cancellation operations.
 *
 * @author Aerobook Platform Engineering
 * @version 1.0.0
 */
@Tag(name = "Booking Operations", description = "Operations for flight ticket reservations, PNR inquiries, manifests, and cancellations")
@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    /**
     * Liveness and readiness health probe for API Gateway routing and cluster
     * orchestrators.
     *
     * @return 200 OK map containing service status, name, port, and timestamp
     */
    @Operation(summary = "Booking Service Health Probe", description = "Liveness probe for API Gateway and container orchestrators.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Booking Service is operational")
    })
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> checkHealth() {
        return ResponseEntity.ok(Map.of(
                "service", "booking-service",
                "status", "UP",
                "port", 8088,
                "timestamp", LocalDateTime.now().toString()
        ));
    }

    /**
     * Creates and confirms a new flight reservation. Validates flight schedule
     * and seat availability via OpenFeign, computes total fare, assigns a
     * unique PNR, saves the booking, and dispatches a RabbitMQ message event.
     *
     * @param request the booking reservation payload
     * @return 200 OK containing confirmed {@link BookingResponse}
     */
    @Operation(
            summary = "Create flight booking",
            description = "Validates flight existence, departure schedule, and seat availability via OpenFeign, "
            + "calculates total fare, assigns a unique PNR, stores reservation, and emits an asynchronous RabbitMQ event.",
            security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Booking confirmed successfully",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookingResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid passenger details, past departure date, or flight unavailable",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Flight not found",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token")
    })
    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Passenger and flight reservation payload",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BookingRequest.class),
                            examples = @ExampleObject(
                                    name = "Single Passenger Booking (Asha Khan)",
                                    summary = "Pre-populated test booking for Flight ID 1",
                                    value = """
                                            {
                                              "userId": 1,
                                              "flightId": 1,
                                              "passengers": [
                                                {
                                                  "firstName": "Asha",
                                                  "lastName": "Khan",
                                                  "age": 29,
                                                  "gender": "F"
                                                }
                                              ]
                                            }
                                            """
                            )
                    )
            )
            @Valid @RequestBody BookingRequest request) {

        return ResponseEntity.ok(bookingService.createBooking(request));
    }

    /**
     * Retrieves all bookings across the entire airline system. Restricted to
     * administrators (ROLE_ADMIN) and staff (ROLE_STAFF).
     *
     * @param userRole optional role passed from API Gateway
     * @return 200 OK list of all bookings or 403 FORBIDDEN
     */
    @Operation(
            summary = "List all bookings across airline (ROLE_ADMIN & ROLE_STAFF)",
            description = "Administrative and ground operations oversight endpoint to view all passenger bookings in the system. Requires ROLE_ADMIN or ROLE_STAFF privilege.",
            security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Bookings retrieved successfully",
                content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BookingResponse.class)))),
        @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Requires ROLE_ADMIN privileges")
    })
    @GetMapping
    public ResponseEntity<?> getAllBookings(
            @Parameter(hidden = true)
            @RequestHeader(value = "X-User-Role", required = false) String userRole) {

        if (userRole != null && !userRole.isEmpty() && !"ROLE_ADMIN".equals(userRole) && !"ROLE_STAFF".equals(userRole)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(
                    LocalDateTime.now(),
                    HttpStatus.FORBIDDEN.value(),
                    "Forbidden",
                    "Access denied: Listing all airline bookings requires ROLE_ADMIN privileges or ROLE_STAFF privileges."
            ));
        }

        return ResponseEntity.ok(bookingService.getAllBookings());
    }

    /**
     * Looks up an individual flight booking reservation by primary key ID.
     *
     * @param bookingId the booking ID
     * @return 200 OK with {@link BookingResponse} or 404 NOT_FOUND
     */
    @Operation(
            summary = "Lookup booking by Database ID",
            description = "Retrieves reservation details, passenger manifest, and flight itinerary by primary booking ID.",
            security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Booking found",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookingResponse.class))),
        @ApiResponse(responseCode = "404", description = "Booking not found with specified ID",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token")
    })
    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingResponse> getBookingById(
            @Parameter(name = "bookingId", description = "Booking primary ID", example = "1")
            @PathVariable Long bookingId) {

        return ResponseEntity.ok(bookingService.getBookingById(bookingId));
    }

    /**
     * Looks up an individual flight booking reservation by its Passenger Name
     * Record (PNR).
     *
     * @param pnr the Passenger Name Record
     * @return 200 OK with {@link BookingResponse} or 404 NOT_FOUND
     */
    @Operation(
            summary = "Lookup booking by PNR",
            description = "Retrieves reservation details, flight information, and passenger manifest using Passenger Name Record (PNR).",
            security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Booking found",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookingResponse.class))),
        @ApiResponse(responseCode = "404", description = "Booking not found with specified PNR",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token")
    })
    @GetMapping("/pnr/{pnr}")
    public ResponseEntity<BookingResponse> getBookingByPnr(
            @Parameter(name = "pnr", description = "Passenger Name Record (e.g. AB1001)", example = "AB1001")
            @PathVariable String pnr) {

        return ResponseEntity.ok(bookingService.getBookingByPnr(pnr));
    }

    /**
     * Retrieves all bookings created by a specific user account.
     *
     * @param userId customer account ID
     * @return 200 OK list of {@link BookingResponse}
     */
    @Operation(
            summary = "Get bookings by user account ID",
            description = "Retrieves all flight reservations booked under a specific user account ID.",
            security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Bookings retrieved successfully",
                content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BookingResponse.class)))),
        @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token")
    })
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BookingResponse>> getBookingsByUser(
            @Parameter(name = "userId", description = "User account ID", example = "1")
            @PathVariable Long userId) {

        return ResponseEntity.ok(bookingService.getBookingsByUserId(userId));
    }

    /**
     * Retrieves all bookings / passenger manifest for a specific flight
     * schedule. Accessible by Airport Ground/Operations Staff (ROLE_STAFF) and
     * Admins (ROLE_ADMIN).
     *
     * @param flightId flight database identifier
     * @param userRole optional role header passed from gateway
     * @return 200 OK list of {@link BookingResponse}
     */
    @Operation(
            summary = "Get flight passenger manifest by flight ID (Staff & Admin)",
            description = "Retrieves all booked reservations and passengers on a specific scheduled flight.",
            security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Passenger manifest retrieved successfully",
                content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BookingResponse.class)))),
        @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Requires ROLE_STAFF or ROLE_ADMIN")
    })
    @GetMapping("/flight/{flightId}")
    public ResponseEntity<?> getBookingsByFlight(
            @Parameter(name = "flightId", description = "Flight database ID", example = "1")
            @PathVariable Long flightId,
            @Parameter(hidden = true)
            @RequestHeader(value = "X-User-Role", required = false) String userRole) {

        if (userRole != null && !userRole.isEmpty() && !"ROLE_ADMIN".equals(userRole) && !"ROLE_STAFF".equals(userRole)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(
                    LocalDateTime.now(),
                    HttpStatus.FORBIDDEN.value(),
                    "Forbidden",
                    "Access denied: Viewing flight passenger manifest requires ROLE_ADMIN or ROLE_STAFF privileges."
            ));
        }

        return ResponseEntity.ok(bookingService.getBookingsByFlightId(flightId));
    }

    /**
     * Cancels an active flight reservation.
     *
     * @param bookingId the database booking ID to cancel
     * @return 200 OK with cancellation message, 400 if already cancelled, 404
     * if not found
     */
    @Operation(
            summary = "Cancel booking",
            description = "Safely cancels a confirmed reservation by its booking ID. Safeguards against double cancellation.",
            security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Booking cancelled successfully"),
        @ApiResponse(responseCode = "400", description = "Booking is already cancelled",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Booking not found with given ID",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token")
    })
    @DeleteMapping("/{bookingId}")
    public ResponseEntity<String> cancelBooking(
            @Parameter(name = "bookingId", description = "Booking database ID to cancel", example = "1")
            @PathVariable Long bookingId) {

        return ResponseEntity.ok(bookingService.cancelBooking(bookingId));
    }
}
