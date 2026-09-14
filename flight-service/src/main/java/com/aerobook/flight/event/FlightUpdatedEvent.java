package com.aerobook.flight.event;

import java.time.LocalDateTime;

/**
 * Event published after flight schedule or route details are modified by
 * administrators.
 */
public class FlightUpdatedEvent {

    /**
     * Database primary key ID of the modified flight.
     */
    private Long flightId;

    /**
     * Unique commercial flight number.
     */
    private String flightNumber;

    /**
     * Updated departure origin.
     */
    private String source;

    /**
     * Updated arrival destination.
     */
    private String destination;

    /**
     * Updated scheduled departure timestamp.
     */
    private LocalDateTime departureTime;

    /**
     * Timestamp when the update was persisted.
     */
    private LocalDateTime updatedAt;

    /**
     * Default no-args constructor for JSON deserialization.
     */
    public FlightUpdatedEvent() {
    }

    /**
     * Constructs a {@link FlightUpdatedEvent} with modified flight parameters.
     *
     * @param flightId database flight ID
     * @param flightNumber flight number
     * @param source origin
     * @param destination destination
     * @param departureTime departure timestamp
     * @param updatedAt modification timestamp
     */
    public FlightUpdatedEvent(Long flightId,
            String flightNumber,
            String source,
            String destination,
            LocalDateTime departureTime,
            LocalDateTime updatedAt) {

        this.flightId = flightId;
        this.flightNumber = flightNumber;
        this.source = source;
        this.destination = destination;
        this.departureTime = departureTime;
        this.updatedAt = updatedAt;
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

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "FlightUpdatedEvent [flightId=" + flightId
                + ", flightNumber=" + flightNumber
                + ", source=" + source
                + ", destination=" + destination
                + ", departureTime=" + departureTime
                + ", updatedAt=" + updatedAt + "]";
    }
}
