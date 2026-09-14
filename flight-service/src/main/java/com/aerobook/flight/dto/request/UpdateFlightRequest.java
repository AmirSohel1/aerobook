package com.aerobook.flight.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Request DTO used for updating existing scheduled flight details.
 */
@Schema(description = "Request payload for modifying scheduled flight details")
public class UpdateFlightRequest {

    /**
     * Operating airline commercial carrier name.
     */
    @Schema(description = "Operating airline name", example = "Air India")
    private String airlineName;

    /**
     * Departure origin city or IATA code.
     */
    @Schema(description = "Departure origin city", example = "Mumbai")
    private String source;

    /**
     * Arrival destination city or IATA code.
     */
    @Schema(description = "Arrival destination city", example = "Delhi")
    private String destination;

    /**
     * Scheduled departure date and time.
     */
    @Schema(description = "Departure timestamp (YYYY-MM-DDTHH:MM:SS)", example = "2026-10-01T10:00:00")
    private LocalDateTime departureTime;

    /**
     * Scheduled arrival date and time.
     */
    @Schema(description = "Arrival timestamp (YYYY-MM-DDTHH:MM:SS)", example = "2026-10-01T12:15:00")
    private LocalDateTime arrivalTime;

    /**
     * Total aircraft seat capacity.
     */
    @Schema(description = "Available passenger seat count", example = "180")
    private Integer totalSeats;

    /**
     * Base ticket fare in INR.
     */
    @Schema(description = "Base ticket fare in INR", example = "5800.0")
    private BigDecimal baseFare;

    /**
     * Default no-args constructor for JSON deserialization.
     */
    public UpdateFlightRequest() {
    }

    /**
     * Constructs an {@link UpdateFlightRequest} with modified parameters.
     *
     * @param airlineName airline name
     * @param source origin
     * @param destination destination
     * @param departureTime departure timestamp
     * @param arrivalTime arrival timestamp
     * @param totalSeats total capacity
     * @param baseFare base fare
     */
    public UpdateFlightRequest(String airlineName, String source,
            String destination,
            LocalDateTime departureTime,
            LocalDateTime arrivalTime,
            Integer totalSeats,
            BigDecimal baseFare) {

        this.airlineName = airlineName;
        this.source = source;
        this.destination = destination;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.totalSeats = totalSeats;
        this.baseFare = baseFare;
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

    @Override
    public String toString() {
        return "UpdateFlightRequest [airlineName=" + airlineName
                + ", source=" + source
                + ", destination=" + destination
                + ", departureTime=" + departureTime
                + ", arrivalTime=" + arrivalTime
                + ", totalSeats=" + totalSeats
                + ", baseFare=" + baseFare + "]";
    }
}
