package com.aerobook.flight.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Response DTO encapsulating flight schedule details, origin, destination,
 * timings, and seat inventory.
 */
@Schema(description = "Flight schedule details response")
public class FlightResponse {

    /**
     * Unique flight database ID.
     */
    @Schema(description = "Unique flight ID", example = "1")
    private Long id;

    /**
     * Commercial flight number.
     */
    @Schema(description = "Flight number", example = "AI101")
    private String flightNumber;

    /**
     * Operating airline commercial carrier.
     */
    @Schema(description = "Operating airline name", example = "Air India")
    private String airlineName;

    /**
     * Departure origin city or IATA code.
     */
    @Schema(description = "Departure origin", example = "Mumbai")
    private String source;

    /**
     * Arrival destination city or IATA code.
     */
    @Schema(description = "Arrival destination", example = "Delhi")
    private String destination;

    /**
     * Scheduled departure date and time.
     */
    @Schema(description = "Departure timestamp", example = "2026-10-01T09:30:00")
    private LocalDateTime departureTime;

    /**
     * Scheduled arrival date and time.
     */
    @Schema(description = "Arrival timestamp", example = "2026-10-01T11:45:00")
    private LocalDateTime arrivalTime;

    /**
     * Total aircraft seating capacity.
     */
    @Schema(description = "Total seat capacity", example = "180")
    private Integer totalSeats;

    /**
     * Currently available passenger seats remaining.
     */
    @Schema(description = "Currently available seats", example = "179")
    private Integer availableSeats;

    /**
     * Base ticket fare in INR.
     */
    @Schema(description = "Base ticket fare in INR", example = "5500.0")
    private BigDecimal baseFare;

    /**
     * Operational status of the flight.
     */
    @Schema(description = "Flight status (e.g. SCHEDULED, COMPLETED, CANCELLED)", example = "SCHEDULED")
    private String status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Integer getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(Integer totalSeats) {
        this.totalSeats = totalSeats;
    }

    public Integer getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(Integer availableSeats) {
        this.availableSeats = availableSeats;
    }

    public BigDecimal getBaseFare() {
        return baseFare;
    }

    public void setBaseFare(BigDecimal baseFare) {
        this.baseFare = baseFare;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
