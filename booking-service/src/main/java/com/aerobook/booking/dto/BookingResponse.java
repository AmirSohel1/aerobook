package com.aerobook.booking.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Transfer Object returned upon booking creation, lookup, or cancellation.
 * Encapsulates booking identification, itinerary metadata, pricing, status, and passenger records.
 */
@Schema(description = "Flight booking reservation details response")
public class BookingResponse {

    /**
     * Database generated primary key for the booking record.
     */
    @Schema(description = "Unique booking database ID", example = "1")
    private Long bookingId;

    /**
     * Six to eight character alphanumeric Passenger Name Record.
     */
    @Schema(description = "Passenger Name Record (PNR) reservation code", example = "PNR-AB1001")
    private String pnr;

    /**
     * Unique ID of the customer account who made the booking.
     */
    @Schema(description = "Customer user account ID", example = "1")
    private Long userId;

    /**
     * Scheduled flight ID associated with this booking.
     */
    @Schema(description = "Scheduled flight database ID", example = "1")
    private Long flightId;

    /**
     * Commercial flight number (e.g. AI101).
     */
    @Schema(description = "Flight number", example = "AI101")
    private String flightNumber;

    /**
     * Operating airline carrier name.
     */
    @Schema(description = "Operating airline name", example = "Air India")
    private String airlineName;

    /**
     * Departure airport origin IATA code or city.
     */
    @Schema(description = "Departure airport / city code", example = "DEL")
    private String source;

    /**
     * Arrival airport destination IATA code or city.
     */
    @Schema(description = "Arrival airport / city code", example = "BOM")
    private String destination;

    /**
     * Scheduled departure date and time.
     */
    @Schema(description = "Scheduled departure timestamp", example = "2026-10-01T06:00:00")
    private LocalDateTime departureTime;

    /**
     * Scheduled arrival date and time.
     */
    @Schema(description = "Scheduled arrival timestamp", example = "2026-10-01T08:15:00")
    private LocalDateTime arrivalTime;

    /**
     * Timestamp when the booking was confirmed.
     */
    @Schema(description = "Booking confirmation timestamp", example = "2026-10-01T08:00:00")
    private LocalDateTime bookingDate;

    /**
     * Total fare billed for all passengers in INR.
     */
    @Schema(description = "Total calculated ticket fare in INR", example = "5500.00")
    private BigDecimal totalFare;

    /**
     * Current lifecycle state of the booking (e.g., BOOKED, CANCELLED).
     */
    @Schema(description = "Booking status (BOOKED, CANCELLED, PENDING)", example = "BOOKED")
    private String status;

    /**
     * Manifest of passengers included in this reservation.
     */
    @Schema(description = "List of registered passengers on this reservation")
    private List<PassengerResponse> passengers = new ArrayList<>();

    /**
     * Default no-args constructor for JSON deserialization.
     */
    public BookingResponse() {
    }

    /**
     * Parametrized constructor initializing core booking attributes.
     *
     * @param bookingId   database booking ID
     * @param pnr         passenger name record
     * @param userId      customer user ID
     * @param flightId    flight ID
     * @param bookingDate confirmation timestamp
     * @param totalFare   total fare amount
     * @param status      reservation status
     */
    public BookingResponse(Long bookingId, String pnr, Long userId, Long flightId,
            LocalDateTime bookingDate, BigDecimal totalFare, String status) {
        this.bookingId = bookingId;
        this.pnr = pnr;
        this.userId = userId;
        this.flightId = flightId;
        this.bookingDate = bookingDate;
        this.totalFare = totalFare;
        this.status = status;
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

    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    public String getAirlineName() {
        return airlineName;
    }

    public void setAirlineName(String airlineName) {
        this.airlineName = airlineName;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public LocalDateTime getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(LocalDateTime departureTime) {
        this.departureTime = departureTime;
    }

    public LocalDateTime getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(LocalDateTime arrivalTime) {
        this.arrivalTime = arrivalTime;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<PassengerResponse> getPassengers() {
        return passengers;
    }

    public void setPassengers(List<PassengerResponse> passengers) {
        this.passengers = passengers;
    }
}
