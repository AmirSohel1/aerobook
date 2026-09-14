package com.aerobook.flight.entity;

import java.time.LocalDateTime;
import java.util.List;

import com.aerobook.flight.enums.AircraftStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Entity class representing Aircraft details.
 *
 * This entity stores information about aircrafts used for operating flights.
 *
 * Table Name : aircrafts
 */
@Schema(description = "Aircraft record entity")
@Entity
@Table(name = "aircrafts")
public class Aircraft {

    /**
     * Unique identifier of aircraft.
     */
    @Schema(description = "Aircraft database ID", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Aircraft code. Example: AI101, INDIGO320
     */
    @Schema(description = "Unique aircraft model registration code", example = "A320-001")
    @Column(name = "aircraft_code", nullable = false, unique = true)
    private String aircraftCode;

    /**
     * Aircraft name.
     */
    @Schema(description = "Aircraft commercial model name", example = "Airbus A320")
    @Column(name = "aircraft_name", nullable = false)
    private String aircraftName;

    /**
     * Manufacturer name.
     */
    @Schema(description = "Manufacturer company", example = "Airbus")
    @Column(nullable = false)
    private String manufacturer;

    /**
     * Passenger seating capacity.
     */
    @Schema(description = "Passenger seating capacity", example = "180")
    @Column(nullable = false)
    private Integer capacity;

    /**
     * Current aircraft status.
     */
    @Schema(description = "Operational status", example = "ACTIVE")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AircraftStatus status;

    /**
     * One aircraft can operate multiple flights.
     */
    @OneToMany(mappedBy = "aircraft", fetch = FetchType.LAZY)
    private List<Flight> flights;

    /**
     * Record creation timestamp.
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /**
     * Record last update timestamp.
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Default constructor required by JPA.
     */
    public Aircraft() {
    }

    /**
     * Parameterized constructor.
     */
    public Aircraft(Long id, String aircraftCode, String aircraftName,
            String manufacturer, Integer capacity,
            AircraftStatus status, List<Flight> flights,
            LocalDateTime createdAt, LocalDateTime updatedAt) {

        this.id = id;
        this.aircraftCode = aircraftCode;
        this.aircraftName = aircraftName;
        this.manufacturer = manufacturer;
        this.capacity = capacity;
        this.status = status;
        this.flights = flights;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * Automatically set timestamps during insert.
     */
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Automatically update timestamp during update.
     */
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // ===========================
    // Getters and Setters
    // ===========================
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAircraftCode() {
        return aircraftCode;
    }

    public void setAircraftCode(String aircraftCode) {
        this.aircraftCode = aircraftCode;
    }

    public String getAircraftName() {
        return aircraftName;
    }

    public void setAircraftName(String aircraftName) {
        this.aircraftName = aircraftName;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public AircraftStatus getStatus() {
        return status;
    }

    public void setStatus(AircraftStatus status) {
        this.status = status;
    }

    public List<Flight> getFlights() {
        return flights;
    }

    public void setFlights(List<Flight> flights) {
        this.flights = flights;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "Aircraft [id=" + id
                + ", aircraftCode=" + aircraftCode
                + ", aircraftName=" + aircraftName
                + ", manufacturer=" + manufacturer
                + ", capacity=" + capacity
                + ", status=" + status
                + "]";
    }
}
