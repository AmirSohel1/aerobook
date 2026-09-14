package com.aerobook.booking.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

/**
 * Passenger details payload for flight reservations.
 * Captures passenger name, age, and gender with strict validation.
 */
@Schema(description = "Passenger details for ticket reservation")
public class PassengerRequest {

    /**
     * Legal first name of the passenger.
     */
    @Schema(description = "Passenger first name", example = "Asha", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Passenger first name is required")
    private String firstName;

    /**
     * Legal last name / surname of the passenger.
     */
    @Schema(description = "Passenger last name", example = "Khan", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Passenger last name is required")
    private String lastName;

    /**
     * Passenger age in years (must be between 1 and 120).
     */
    @Schema(description = "Passenger age in years (1 - 120)", example = "28", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Passenger age is required")
    @Min(value = 1, message = "Passenger age must be at least 1")
    @Max(value = 120, message = "Passenger age must not exceed 120")
    private Integer age;

    /**
     * Passenger gender code: 'M' (Male), 'F' (Female), or 'O' (Other).
     */
    @Schema(description = "Passenger gender (M / F / O)", example = "F", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Gender is required")
    @Pattern(regexp = "^(?i)[MFO]$", message = "Gender must be 'M', 'F', or 'O'")
    private String gender;

    /**
     * Default no-args constructor for JSON deserialization.
     */
    public PassengerRequest() {
    }

    /**
     * Parametrized constructor initializing all passenger details.
     *
     * @param firstName legal first name
     * @param lastName  legal last name
     * @param age       age in years
     * @param gender    gender identifier ('M', 'F', or 'O')
     */
    public PassengerRequest(String firstName, String lastName, Integer age, String gender) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.gender = gender;
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
}
