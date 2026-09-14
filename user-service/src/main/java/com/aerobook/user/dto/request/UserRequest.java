package com.aerobook.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;

/**
 * ============================================================================
 * User Profile Creation / Mutation Request DTO
 * ============================================================================
 *
 * Incoming HTTP request body containing user profile coordinates and personal
 * demographic attributes.
 *
 * @author Aerobook Platform Engineering
 * @version 1.0.0
 */
@Schema(description = "Request payload for creating or updating user profile details")
public class UserRequest {

    /**
     * Customer legal first given name.
     */
    @Schema(description = "User's legal first name", example = "Ravi", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "First name is required")
    private String firstName;

    /**
     * Customer legal last family name.
     */
    @Schema(description = "User's legal last name", example = "Patel", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Last name is required")
    private String lastName;

    /**
     * Customer primary electronic mail address used for login and flight
     * communications.
     */
    @Schema(description = "Primary email address used for login and notifications", example = "ravi.patel@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be a valid format")
    private String email;

    /**
     * 10-15 digit telephone contact number.
     */
    @Schema(description = "Contact phone number (10-15 digits)", example = "9876543212", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[0-9]{10,15}$", message = "Phone number must be between 10 and 15 digits")
    private String phoneNumber;

    /**
     * Customer date of birth (ISO-8601: YYYY-MM-DD).
     */
    @Schema(description = "Date of birth (YYYY-MM-DD)", example = "1995-09-20")
    private LocalDate dateOfBirth;

    /**
     * Customer citizenship country name.
     */
    @Schema(description = "Nationality of the customer", example = "Indian")
    private String nationality;

    /**
     * Desired or assigned security authority role (ROLE_USER or ROLE_ADMIN).
     */
    @Schema(description = "Security role authority (ROLE_USER or ROLE_ADMIN)", example = "ROLE_USER")
    private String role;

    /**
     * Default no-argument constructor.
     */
    public UserRequest() {
    }

    /**
     * Full-parameter constructor for manual instantiation.
     *
     * @param firstName customer first name
     * @param lastName customer last name
     * @param email customer email
     * @param phoneNumber contact phone
     * @param dateOfBirth date of birth
     * @param nationality nationality
     * @param role security authority role
     */
    public UserRequest(String firstName, String lastName, String email, String phoneNumber,
            LocalDate dateOfBirth, String nationality, String role) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.dateOfBirth = dateOfBirth;
        this.nationality = nationality;
        this.role = role;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
