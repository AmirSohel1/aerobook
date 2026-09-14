package com.aerobook.flight.dto.response;

/**
 * Response DTO returning real-time seat availability information for a flight.
 */
@io.swagger.v3.oas.annotations.media.Schema(description = "Flight seat availability query response")
public class SeatAvailabilityResponse {

    /**
     * Unique flight database ID.
     */
    @io.swagger.v3.oas.annotations.media.Schema(description = "Flight database ID", example = "1")
    private Long flightId;

    /**
     * Commercial flight number.
     */
    @io.swagger.v3.oas.annotations.media.Schema(description = "Flight number", example = "AI101")
    private String flightNumber;

    /**
     * Total aircraft passenger seat capacity.
     */
    @io.swagger.v3.oas.annotations.media.Schema(description = "Total seating capacity", example = "180")
    private Integer totalSeats;

    /**
     * Unreserved passenger seats remaining.
     */
    @io.swagger.v3.oas.annotations.media.Schema(description = "Currently available seat count", example = "179")
    private Integer availableSeats;

    /**
     * Boolean indicator whether any seats remain for new reservations.
     */
    @io.swagger.v3.oas.annotations.media.Schema(description = "Flag indicating if seats are available", example = "true")
    private Boolean seatsAvailable;

    /**
     * Default no-args constructor for JSON serialization.
     */
    public SeatAvailabilityResponse() {
    }

    /**
     * Parameterized constructor initializing seat availability fields.
     *
     * @param flightId flight ID
     * @param flightNumber flight number
     * @param totalSeats total seats
     * @param availableSeats available seats
     * @param seatsAvailable availability flag
     */
    public SeatAvailabilityResponse(Long flightId,
            String flightNumber,
            Integer totalSeats,
            Integer availableSeats,
            Boolean seatsAvailable) {

        this.flightId = flightId;
        this.flightNumber = flightNumber;
        this.totalSeats = totalSeats;
        this.availableSeats = availableSeats;
        this.seatsAvailable = seatsAvailable;
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

    public Boolean getSeatsAvailable() {
        return seatsAvailable;
    }

    public void setSeatsAvailable(Boolean seatsAvailable) {
        this.seatsAvailable = seatsAvailable;
    }

    @Override
    public String toString() {
        return "SeatAvailabilityResponse [flightId=" + flightId
                + ", flightNumber=" + flightNumber
                + ", totalSeats=" + totalSeats
                + ", availableSeats=" + availableSeats
                + ", seatsAvailable=" + seatsAvailable + "]";
    }
}
