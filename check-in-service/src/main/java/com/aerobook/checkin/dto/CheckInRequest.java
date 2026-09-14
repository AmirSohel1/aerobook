package com.aerobook.checkin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * ============================================================================
 * Check-In Execution Request DTO
 * ============================================================================
 *
 * Incoming payload submitted by passengers or check-in agents to initiate
 * airport check-in, verify passenger identity, and assign a physical cabin seat.
 *
 * @author Aerobook Platform Engineering
 * @version 1.0.0
 */
@Schema(description = "Passenger check-in request payload")
public class CheckInRequest {

    /**
     * Unique ID of the confirmed ticket booking reservation.
     */
    @Schema(description = "Confirmed booking database ID", example = "1")
    @NotNull(message = "Booking ID is required")
    private Long bookingId;

    /**
     * Passenger legal name exactly matching the reservation record.
     */
    @Schema(description = "Passenger full name as recorded on reservation", example = "Asha Khan")
    @NotBlank(message = "Passenger name cannot be blank")
    @Size(min = 2, max = 100, message = "Passenger name must be between 2 and 100 characters")
    private String passengerName;

    /**
     * Desired cabin seat number (optional; defaults to auto-allocation if null).
     */
    @Schema(description = "Requested seat number (e.g. 12A, 14C)", example = "12A")
    private String seatNumber;

    /**
     * Default no-argument constructor.
     */
    public CheckInRequest() {
    }

    /**
     * Parameterized constructor.
     *
     * @param bookingId linked booking ID
     * @param passengerName passenger full name
     * @param seatNumber seat number
     */
    public CheckInRequest(Long bookingId, String passengerName, String seatNumber) {
        this.bookingId = bookingId;
        this.passengerName = passengerName;
        this.seatNumber = seatNumber;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public String getPassengerName() {
        return passengerName;
    }

    public void setPassengerName(String passengerName) {
        this.passengerName = passengerName;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }
}
