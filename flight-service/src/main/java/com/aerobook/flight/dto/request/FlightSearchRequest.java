package com.aerobook.flight.dto.request;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Request DTO used for searching flights. Contains search criteria entered by
 * the user while querying available flights.
 */
@io.swagger.v3.oas.annotations.media.Schema(description = "Flight route and schedule search query parameters")
public class FlightSearchRequest {

    /**
     * Departure origin city or IATA code.
     */
    @io.swagger.v3.oas.annotations.media.Schema(description = "Departure origin city", example = "Mumbai")
    @NotBlank(message = "Source is required")
    private String source;

    /**
     * Arrival destination city or IATA code.
     */
    @io.swagger.v3.oas.annotations.media.Schema(description = "Arrival destination city", example = "Delhi")
    @NotBlank(message = "Destination is required")
    private String destination;

    /**
     * Date of intended travel.
     */
    @io.swagger.v3.oas.annotations.media.Schema(description = "Travel date (YYYY-MM-DD)", example = "2026-10-01")
    @NotNull(message = "Travel date is required")
    private LocalDate travelDate;

    /**
     * Number of passengers travelling together.
     */
    @io.swagger.v3.oas.annotations.media.Schema(description = "Passenger headcount", example = "1")
    @NotNull(message = "Passenger count is required")
    private Integer passengers;

    /**
     * Default constructor for JSON deserialization.
     */
    public FlightSearchRequest() {
    }

    /**
     * Parameterized constructor.
     *
     * @param source Departure location
     * @param destination Arrival location
     * @param travelDate Travel date
     * @param passengers Number of passengers
     */
    public FlightSearchRequest(String source,
            String destination,
            LocalDate travelDate,
            Integer passengers) {
        this.source = source;
        this.destination = destination;
        this.travelDate = travelDate;
        this.passengers = passengers;
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

    public LocalDate getTravelDate() {
        return travelDate;
    }

    public void setTravelDate(LocalDate travelDate) {
        this.travelDate = travelDate;
    }

    public Integer getPassengers() {
        return passengers;
    }

    public void setPassengers(Integer passengers) {
        this.passengers = passengers;
    }

    @Override
    public String toString() {
        return "FlightSearchRequest [source=" + source
                + ", destination=" + destination
                + ", travelDate=" + travelDate
                + ", passengers=" + passengers + "]";
    }
}
