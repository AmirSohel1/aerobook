package com.aerobook.booking.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * Request payload for creating a new flight ticket booking.
 * Enforces non-null customer ID, flight ID, and at least one passenger.
 */
@Schema(description = "Request payload for reserving flight tickets")
public class BookingRequest {

    /**
     * Unique identifier of the authenticated customer making the reservation.
     */
    @Schema(description = "Customer user account ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "User ID is required")
    private Long userId;

    /**
     * Unique identifier of the scheduled flight being booked.
     */
    @Schema(description = "Scheduled flight database ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Flight ID is required")
    private Long flightId;

    /**
     * List of passengers travelling on this reservation.
     */
    @Schema(description = "List of passengers travelling on this reservation", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "At least one passenger is required")
    @Valid
    private List<PassengerRequest> passengers = new ArrayList<>();

    /**
     * Default no-args constructor for JSON deserialization.
     */
    public BookingRequest() {
    }

    /**
     * Constructs a {@link BookingRequest} with all required fields.
     *
     * @param userId     customer user ID
     * @param flightId   flight database ID
     * @param passengers list of passenger details
     */
    public BookingRequest(Long userId, Long flightId, List<PassengerRequest> passengers) {
        this.userId = userId;
        this.flightId = flightId;
        this.passengers = passengers != null ? passengers : new ArrayList<>();
    }

    /**
     * Gets the customer user ID.
     *
     * @return the user ID
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * Sets the customer user ID.
     *
     * @param userId the user ID
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * Gets the flight ID.
     *
     * @return the flight ID
     */
    public Long getFlightId() {
        return flightId;
    }

    /**
     * Sets the flight ID.
     *
     * @param flightId the flight ID
     */
    public void setFlightId(Long flightId) {
        this.flightId = flightId;
    }

    /**
     * Gets the list of passengers.
     *
     * @return list of {@link PassengerRequest}
     */
    public List<PassengerRequest> getPassengers() {
        return passengers;
    }

    /**
     * Sets the list of passengers.
     *
     * @param passengers list of {@link PassengerRequest}
     */
    public void setPassengers(List<PassengerRequest> passengers) {
        this.passengers = passengers;
    }
}
