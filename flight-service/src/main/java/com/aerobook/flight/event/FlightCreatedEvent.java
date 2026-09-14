package com.aerobook.flight.event;

import java.time.LocalDateTime;

/**
 * Event published across the microservices ecosystem after successful flight
 * schedule creation.
 */
public class FlightCreatedEvent {

    /**
     * Database primary key ID of the newly scheduled flight.
     */
    private Long flightId;

    /**
     * Unique commercial flight number (e.g. AI101).
     */
    private String flightNumber;

    /**
     * Departure airport origin IATA code or city.
     */
    private String source;

    /**
     * Arrival airport destination IATA code or city.
     */
    private String destination;

    /**
     * Scheduled departure date and time.
     */
    private LocalDateTime departureTime;

    /**
     * Default no-args constructor for JSON deserialization.
     */
    public FlightCreatedEvent() {
    }

    /**
     * Constructs a {@link FlightCreatedEvent} with scheduled flight attributes.
     *
     * @param flightId database flight ID
     * @param flightNumber commercial flight identifier
     * @param source departure origin
     * @param destination arrival destination
     * @param departureTime scheduled departure timestamp
     */
    public FlightCreatedEvent(Long flightId,
            String flightNumber,
            String source,
            String destination,
            LocalDateTime departureTime) {

        this.flightId = flightId;
        this.flightNumber = flightNumber;
        this.source = source;
        this.destination = destination;
        this.departureTime = departureTime;
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

    @Override
    public String toString() {
        return "FlightCreatedEvent [flightId=" + flightId
                + ", flightNumber=" + flightNumber
                + ", source=" + source
                + ", destination=" + destination
                + ", departureTime=" + departureTime + "]";
    }
}
