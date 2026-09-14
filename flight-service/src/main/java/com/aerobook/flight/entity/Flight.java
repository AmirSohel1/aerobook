package com.aerobook.flight.entity;

import com.aerobook.flight.enums.FlightStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Persistent flight schedule and availability record. Represents commercial
 * flights operated across city pairs with seat inventory tracking.
 *
 * Table Name: flights
 */
@Entity
@Table(name = "flights")
public class Flight {

    /**
     * Unique database primary key ID.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Unique commercial flight identifier (e.g. AI101).
     */
    @Column(name = "flight_number", nullable = false, unique = true)
    private String flightNumber;

    /**
     * Name of the operating airline carrier (e.g. Air India).
     */
    @Column(name = "airline_name", nullable = false)
    private String airlineName;

    /**
     * Departure city or IATA airport code.
     */
    @Column(nullable = false)
    private String source;

    /**
     * Arrival destination city or IATA airport code.
     */
    @Column(nullable = false)
    private String destination;

    /**
     * Scheduled departure date and time.
     */
    @Column(name = "departure_time", nullable = false)
    private LocalDateTime departureTime;

    /**
     * Scheduled arrival date and time.
     */
    @Column(name = "arrival_time", nullable = false)
    private LocalDateTime arrivalTime;

    /**
     * Total aircraft passenger seat capacity.
     */
    @Column(name = "total_seats", nullable = false)
    private Integer totalSeats;

    /**
     * Real-time available seats remaining for booking.
     */
    @Column(name = "available_seats", nullable = false)
    private Integer availableSeats;

    /**
     * Base ticket fare per passenger before taxes and dynamic pricing.
     */
    @Column(name = "base_fare", nullable = false, precision = 12, scale = 2)
    private BigDecimal baseFare;

    /**
     * Operational flight status (SCHEDULED, DELAYED, CANCELLED, COMPLETED).
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FlightStatus status;

    /**
     * Operating aircraft assigned from fleet inventory. Prevent circular JSON
     * when an aircraft is returned by its controller.
     */
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "aircraft_id", nullable = false)
    private Aircraft aircraft;

    /**
     * Default no-args constructor required by JPA.
     */
    public Flight() {
    }

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

    public FlightStatus getStatus() {
        return status;
    }

    public void setStatus(FlightStatus status) {
        this.status = status;
    }

    public Aircraft getAircraft() {
        return aircraft;
    }

    public void setAircraft(Aircraft aircraft) {
        this.aircraft = aircraft;
    }
}
