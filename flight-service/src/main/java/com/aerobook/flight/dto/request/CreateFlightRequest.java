package com.aerobook.flight.dto.request;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Request DTO used for creating a new flight.
 *
 * This class receives flight information from the client and transfers it to
 * the service layer.
 */
@Schema(description = "Request payload for creating a new scheduled flight")
public class CreateFlightRequest {

    /**
     * Unique flight number.
     */
    @Schema(description = "Flight schedule number", example = "AI101")
    @NotBlank(message = "Flight number is required")
    private String flightNumber;

    /**
     * Airline operating the flight.
     */
    @Schema(description = "Operating airline name", example = "Air India")
    @NotBlank(message = "Airline name is required")
    private String airlineName;

    /**
     * Flight source location.
     */
    @Schema(description = "Departure origin city", example = "Mumbai")
    @NotBlank(message = "Source is required")
    private String source;

    /**
     * Flight destination location.
     */
    @Schema(description = "Arrival destination city", example = "Delhi")
    @NotBlank(message = "Destination is required")
    private String destination;

    /**
     * Flight departure date and time.
     */
    @Schema(description = "Departure timestamp (YYYY-MM-DDTHH:MM:SS)", example = "2026-10-01T09:30:00")
    @NotNull(message = "Departure time is required")
    private LocalDateTime departureTime;

    /**
     * Flight arrival date and time.
     */
    @Schema(description = "Arrival timestamp (YYYY-MM-DDTHH:MM:SS)", example = "2026-10-01T11:45:00")
    @NotNull(message = "Arrival time is required")
    private LocalDateTime arrivalTime;

    /**
     * Total seating capacity.
     */
    @Schema(description = "Available passenger seat count", example = "180")
    @NotNull(message = "Total seats is required")
    private Integer totalSeats;

    /**
     * Base fare of flight.
     */
    @Schema(description = "Base ticket fare in INR", example = "5500.0")
    @NotNull(message = "Base fare is required")
    private BigDecimal baseFare;

    /**
     * Aircraft identifier assigned to the flight.
     */
    @Schema(description = "Database ID of assigned aircraft", example = "1")
    @NotNull(message = "Aircraft Id is required")
    private Long aircraftId;

    /**
     * Default constructor for JSON deserialization.
     */
    public CreateFlightRequest() {
    }

    /**
     * Parameterized constructor initializing all flight schedule fields.
     *
     * @param flightNumber flight commercial number
     * @param airlineName operating airline carrier
     * @param source origin city / IATA code
     * @param destination arrival city / IATA code
     * @param departureTime scheduled departure timestamp
     * @param arrivalTime scheduled arrival timestamp
     * @param totalSeats total aircraft seating capacity
     * @param baseFare base fare amount in INR
     * @param aircraftId database ID of assigned aircraft
     */
    public CreateFlightRequest(
            String flightNumber,
            String airlineName,
            String source,
            String destination,
            LocalDateTime departureTime,
            LocalDateTime arrivalTime,
            Integer totalSeats,
            BigDecimal baseFare,
            Long aircraftId) {

        this.flightNumber = flightNumber;
        this.airlineName = airlineName;
        this.source = source;
        this.destination = destination;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.totalSeats = totalSeats;
        this.baseFare = baseFare;
        this.aircraftId = aircraftId;
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

    public BigDecimal getBaseFare() {
        return baseFare;
    }

    public void setBaseFare(BigDecimal baseFare) {
        this.baseFare = baseFare;
    }

    public Long getAircraftId() {
        return aircraftId;
    }

    public void setAircraftId(Long aircraftId) {
        this.aircraftId = aircraftId;
    }

    @Override
    public String toString() {
        return "CreateFlightRequest [flightNumber=" + flightNumber
                + ", airlineName=" + airlineName
                + ", source=" + source
                + ", destination=" + destination
                + ", departureTime=" + departureTime
                + ", arrivalTime=" + arrivalTime
                + ", totalSeats=" + totalSeats
                + ", baseFare=" + baseFare
                + ", aircraftId=" + aircraftId + "]";
    }
}
