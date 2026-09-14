package com.aerobook.flight.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Response DTO returned after flight schedule route search.
 */
@io.swagger.v3.oas.annotations.media.Schema(description = "Flight search route match result")
public class FlightSearchResponse {

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
     * Operating airline commercial carrier.
     */
    @io.swagger.v3.oas.annotations.media.Schema(description = "Airline name", example = "Air India")
    private String airlineName;

    /**
     * Departure origin city or IATA code.
     */
    @io.swagger.v3.oas.annotations.media.Schema(description = "Departure origin", example = "Mumbai")
    private String source;

    /**
     * Arrival destination city or IATA code.
     */
    @io.swagger.v3.oas.annotations.media.Schema(description = "Arrival destination", example = "Delhi")
    private String destination;

    /**
     * Scheduled departure timestamp.
     */
    @io.swagger.v3.oas.annotations.media.Schema(description = "Departure timestamp", example = "2026-10-01T09:30:00")
    private LocalDateTime departureTime;

    /**
     * Scheduled arrival timestamp.
     */
    @io.swagger.v3.oas.annotations.media.Schema(description = "Arrival timestamp", example = "2026-10-01T11:45:00")
    private LocalDateTime arrivalTime;

    /**
     * Available seats remaining on this flight.
     */
    @io.swagger.v3.oas.annotations.media.Schema(description = "Available seats", example = "179")
    private Integer availableSeats;

    /**
     * Calculated ticket fare for the requested travel itinerary.
     */
    @io.swagger.v3.oas.annotations.media.Schema(description = "Fare price in INR", example = "5500.00")
    private BigDecimal fare;

    /**
     * Default no-args constructor for JSON serialization.
     */
    public FlightSearchResponse() {
    }

    /**
     * Parameterized constructor initializing search response fields.
     *
     * @param flightId flight ID
     * @param flightNumber flight number
     * @param airlineName airline name
     * @param source origin
     * @param destination destination
     * @param departureTime departure timestamp
     * @param arrivalTime arrival timestamp
     * @param availableSeats seat availability
     * @param fare ticket fare
     */
    public FlightSearchResponse(Long flightId, String flightNumber,
            String airlineName, String source,
            String destination,
            LocalDateTime departureTime,
            LocalDateTime arrivalTime,
            Integer availableSeats,
            BigDecimal fare) {

        this.flightId = flightId;
        this.flightNumber = flightNumber;
        this.airlineName = airlineName;
        this.source = source;
        this.destination = destination;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.availableSeats = availableSeats;
        this.fare = fare;
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

    public Integer getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(Integer availableSeats) {
        this.availableSeats = availableSeats;
    }

    public BigDecimal getFare() {
        return fare;
    }

    public void setFare(BigDecimal fare) {
        this.fare = fare;
    }

    @Override
    public String toString() {
        return "FlightSearchResponse [flightId=" + flightId
                + ", flightNumber=" + flightNumber
                + ", airlineName=" + airlineName
                + ", source=" + source
                + ", destination=" + destination
                + ", departureTime=" + departureTime
                + ", arrivalTime=" + arrivalTime
                + ", availableSeats=" + availableSeats
                + ", fare=" + fare + "]";
    }
}
