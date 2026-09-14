package com.aerobook.flight.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Request DTO used for creating and onboarding a new aircraft into the fleet.
 */
@Schema(description = "Request payload for registering a new aircraft in the fleet")
public class CreateAircraftRequest {

    /**
     * Unique identifier code for the aircraft model or tail registration.
     */
    @Schema(description = "Unique aircraft model code", example = "A320-001")
    @NotBlank(message = "Aircraft code is required")
    private String aircraftCode;

    /**
     * Commercial model or fleet name.
     */
    @Schema(description = "Commercial aircraft name", example = "Airbus A320")
    @NotBlank(message = "Aircraft name is required")
    private String aircraftName;

    /**
     * Commercial manufacturer of the aircraft.
     */
    @Schema(description = "Aircraft manufacturer", example = "Airbus")
    @NotBlank(message = "Manufacturer is required")
    private String manufacturer;

    /**
     * Maximum passenger seating capacity.
     */
    @Schema(description = "Maximum passenger seating capacity", example = "180")
    @NotNull(message = "Capacity is required")
    private Integer capacity;

    /**
     * Default no-args constructor for JSON deserialization.
     */
    public CreateAircraftRequest() {
    }

    /**
     * Constructs a {@link CreateAircraftRequest} with full aircraft parameters.
     *
     * @param aircraftCode unique aircraft registration code
     * @param aircraftName commercial model name
     * @param manufacturer aerospace manufacturer
     * @param capacity total passenger seat capacity
     */
    public CreateAircraftRequest(String aircraftCode, String aircraftName,
            String manufacturer, Integer capacity) {
        this.aircraftCode = aircraftCode;
        this.aircraftName = aircraftName;
        this.manufacturer = manufacturer;
        this.capacity = capacity;
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

    @Override
    public String toString() {
        return "CreateAircraftRequest [aircraftCode=" + aircraftCode
                + ", aircraftName=" + aircraftName
                + ", manufacturer=" + manufacturer
                + ", capacity=" + capacity;
    }
}
