package com.aerobook.booking.entity;

import com.aerobook.booking.entity.enums.BookingStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * ============================================================================
 * Flight Reservation JPA Entity
 * ============================================================================
 *
 * Primary entity mapping to the {@code bookings} table in
 * {@code aerobook_booking_db}. Stores reservation details including generated
 * PNR, user ownership, flight reference, total calculated fare, booking status,
 * and attached passenger roster.
 *
 * @author Aerobook Platform Engineering
 * @version 1.0.0
 */
@Entity
@Table(name = "bookings")
public class Booking {

    /**
     * Unique auto-increment primary identifier.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookingId;

    /**
     * Passenger Name Record (PNR) code (unique 6-12 character alphanumeric
     * string).
     */
    @Column(nullable = false, unique = true, length = 20)
    private String pnr;

    /**
     * Customer user account identifier owning this reservation.
     */
    @Column(nullable = false)
    private Long userId;

    /**
     * Target scheduled flight identifier.
     */
    @Column(nullable = false)
    private Long flightId;

    /**
     * Timestamp recording when reservation was confirmed.
     */
    @Column(nullable = false)
    private LocalDateTime bookingDate;

    /**
     * Total calculated ticket fare (base price + taxes - discounts).
     */
    @Column(nullable = false)
    private BigDecimal totalFare;

    /**
     * Operational state of the reservation (e.g. BOOKED, CANCELLED).
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BookingStatus status;

    /**
     * Child collection of passengers traveling under this booking reservation.
     */
    @OneToMany(
            mappedBy = "booking",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Passenger> passengers = new ArrayList<>();

    /**
     * Default no-argument constructor required by JPA.
     */
    public Booking() {
    }

    /**
     * Full-parameter constructor.
     *
     * @param bookingId booking ID
     * @param pnr generated PNR string
     * @param userId customer user ID
     * @param flightId flight schedule ID
     * @param bookingDate reservation timestamp
     * @param totalFare total monetary price
     * @param status booking status
     * @param passengers passenger list
     */
    public Booking(Long bookingId, String pnr, Long userId, Long flightId,
            LocalDateTime bookingDate, BigDecimal totalFare,
            BookingStatus status, List<Passenger> passengers) {
        this.bookingId = bookingId;
        this.pnr = pnr;
        this.userId = userId;
        this.flightId = flightId;
        this.bookingDate = bookingDate;
        this.totalFare = totalFare;
        this.status = status;
        this.passengers = passengers != null ? passengers : new ArrayList<>();
    }

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public String getPnr() {
        return pnr;
    }

    public void setPnr(String pnr) {
        this.pnr = pnr;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getFlightId() {
        return flightId;
    }

    public void setFlightId(Long flightId) {
        this.flightId = flightId;
    }

    public LocalDateTime getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(LocalDateTime bookingDate) {
        this.bookingDate = bookingDate;
    }

    public BigDecimal getTotalFare() {
        return totalFare;
    }

    public void setTotalFare(BigDecimal totalFare) {
        this.totalFare = totalFare;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public List<Passenger> getPassengers() {
        return passengers;
    }

    public void setPassengers(List<Passenger> passengers) {
        this.passengers = passengers;
    }
}
