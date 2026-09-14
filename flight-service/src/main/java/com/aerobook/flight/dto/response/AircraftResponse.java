package com.aerobook.flight.dto.response;

/**
 * Response DTO used to return aircraft details and operational status.
 */
@io.swagger.v3.oas.annotations.media.Schema(description = "Aircraft fleet record response")
public class AircraftResponse {

    /**
     * Aircraft database primary key.
     */
    @io.swagger.v3.oas.annotations.media.Schema(description = "Aircraft database ID", example = "1")
    private Long id;

    /**
     * Unique aircraft registration code.
     */
    @io.swagger.v3.oas.annotations.media.Schema(description = "Aircraft registration code", example = "A320-001")
    private String aircraftCode;

    /**
     * Commercial aircraft model name.
     */
    @io.swagger.v3.oas.annotations.media.Schema(description = "Aircraft commercial model name", example = "Airbus A320")
    private String aircraftName;

    /**
     * Aerospace manufacturing company.
     */
    @io.swagger.v3.oas.annotations.media.Schema(description = "Manufacturer name", example = "Airbus")
    private String manufacturer;

    /**
     * Maximum passenger seat capacity.
     */
    @io.swagger.v3.oas.annotations.media.Schema(description = "Maximum seating capacity", example = "180")
    private Integer capacity;

    /**
     * Fleet operational status (ACTIVE, INACTIVE, MAINTENANCE).
     */
    @io.swagger.v3.oas.annotations.media.Schema(description = "Aircraft status", example = "ACTIVE")
    private String status;

    /**
     * Default no-args constructor for JSON serialization.
     */
    public AircraftResponse() {
    }

    /**
     * Parameterized constructor initializing all aircraft response fields.
     *
     * @param id database ID
     * @param aircraftCode model code
     * @param aircraftName commercial model name
     * @param manufacturer manufacturer
     * @param capacity passenger capacity
     * @param status operational status
     */
    public AircraftResponse(Long id, String aircraftCode, String aircraftName,
            String manufacturer, Integer capacity,
            String status) {
        this.id = id;
        this.aircraftCode = aircraftCode;
        this.aircraftName = aircraftName;
        this.manufacturer = manufacturer;
        this.capacity = capacity;
        this.status = status;
    }

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "AircraftResponse [id=" + id
                + ", aircraftCode=" + aircraftCode
                + ", aircraftName=" + aircraftName
                + ", manufacturer=" + manufacturer
                + ", capacity=" + capacity
                + ", status=" + status + "]";
    }
}
