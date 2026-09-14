package com.aerobook.checkin.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * ============================================================================
 * Check-In Confirmation & Boarding Pass JPA Entity
 * ============================================================================
 *
 * Persists passenger check-in verification records, assigned seat allocations,
 * and generated boarding pass numbers in the {@code check_in} table.
 *
 * @author Aerobook Platform Engineering
 * @version 1.0.0
 */
@Schema(description = "Check-in confirmation details")
@Entity
@Table(name = "check_in")
public class CheckIn {

    /**
     * Unique auto-increment primary key for the check-in record.
     */
    @Schema(description = "Unique check-in record ID", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Foreign booking identifier linking this check-in to an active reservation.
     */
    @Schema(description = "Associated booking ID", example = "1")
    private Long bookingId;

    /**
     * Passenger full legal name as confirmed during airport check-in.
     */
    @Schema(description = "Passenger full name", example = "Asha Khan")
    private String passengerName;

    /**
     * Physical cabin seat assigned to the passenger (e.g., "12A").
     */
    @Schema(description = "Assigned seat number", example = "12A")
    private String seatNumber;

    /**
     * Official alphanumeric boarding pass identifier (e.g., "BP-1-12A").
     */
    @Schema(description = "Generated boarding pass reference number", example = "BP-1001-12A")
    private String boardingPassNumber;

    /**
     * Current operational status of the check-in record (e.g., "CHECKED_IN").
     */
    @Schema(description = "Check-in status", example = "CHECKED_IN")
    private String status;

    /**
     * Timestamp recording when check-in was confirmed.
     */
    @Schema(description = "Timestamp when check-in occurred", example = "2026-10-01T08:30:00")
    private LocalDateTime checkedInAt;

    /**
     * Default no-args constructor required by JPA.
     */
    public CheckIn() {
    }

    /**
     * Parameterized constructor initializing all check-in attributes.
     *
     * @param id primary key ID
     * @param bookingId linked booking ID
     * @param passengerName passenger name
     * @param seatNumber allocated seat
     * @param boardingPassNumber boarding pass string
     * @param status check-in state
     * @param checkedInAt confirmation timestamp
     */
    public CheckIn(Long id, Long bookingId, String passengerName, String seatNumber, String boardingPassNumber, String status, LocalDateTime checkedInAt) {
        this.id = id;
        this.bookingId = bookingId;
        this.passengerName = passengerName;
        this.seatNumber = seatNumber;
        this.boardingPassNumber = boardingPassNumber;
        this.status = status;
        this.checkedInAt = checkedInAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getBoardingPassNumber() {
        return boardingPassNumber;
    }

    public void setBoardingPassNumber(String boardingPassNumber) {
        this.boardingPassNumber = boardingPassNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCheckedInAt() {
        return checkedInAt;
    }

    public void setCheckedInAt(LocalDateTime checkedInAt) {
        this.checkedInAt = checkedInAt;
    }
}
