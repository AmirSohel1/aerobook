package com.aerobook.booking.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Data Transfer Object representing passenger details within a confirmed booking.
 * Includes passenger identity attributes and allocated seat number.
 */
@Schema(description = "Passenger details in booking response")
public class PassengerResponse {

    /**
     * Database primary key of the passenger record.
     */
    @Schema(description = "Unique passenger ID", example = "1")
    private Long passengerId;

    /**
     * First name of the passenger.
     */
    @Schema(description = "Passenger first name", example = "Asha")
    private String firstName;

    /**
     * Last name of the passenger.
     */
    @Schema(description = "Passenger last name", example = "Khan")
    private String lastName;

    /**
     * Age of the passenger in years.
     */
    @Schema(description = "Passenger age", example = "29")
    private Integer age;

    /**
     * Gender of the passenger (M, F, O).
     */
    @Schema(description = "Passenger gender (M, F, O)", example = "F")
    private String gender;

    /**
     * Assigned airline seat number (e.g. 12A).
     */
    @Schema(description = "Assigned seat number", example = "12A")
    private String seatNumber;

    /**
     * Default no-args constructor for JSON deserialization.
     */
    public PassengerResponse() {
    }

    /**
     * Parametrized constructor initializing all passenger response fields.
     *
     * @param passengerId database passenger ID
     * @param firstName   first name
     * @param lastName    last name
     * @param age         age in years
     * @param gender      gender code
     * @param seatNumber  assigned seat code
     */
    public PassengerResponse(Long passengerId, String firstName, String lastName, Integer age, String gender, String seatNumber) {
        this.passengerId = passengerId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.gender = gender;
        this.seatNumber = seatNumber;
    }

    public Long getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(Long passengerId) {
        this.passengerId = passengerId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }
}
