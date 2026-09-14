package com.aerobook.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

/**
 * ============================================================================
 * User Registration Request Data Transfer Object
 * ============================================================================
 *
 * Submitted to {@code /api/auth/register} or {@code /api/auth/register-admin}
 * to create user credentials and initial profile records.
 *
 * @author Aerobook Security Engineering
 * @version 1.0.0
 */
@Schema(description = "Request payload for registering a new user or administrator")
public class RegisterRequest {

    /**
     * Customer first name.
     */
    @Schema(description = "User's first name", example = "Asha")
    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;

    /**
     * Customer last name.
     */
    @Schema(description = "User's last name", example = "Khan")
    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastName;

    /**
     * Unique email address for system authentication.
     */
    @Schema(description = "User's email address", example = "asha.khan@example.com")
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be a valid format")
    private String email;

    /**
     * Primary contact phone number.
     */
    @Schema(description = "Contact phone number", example = "9876543210")
    @NotBlank(message = "Phone number is required")
    private String phoneNumber;

    /**
     * Date of birth (YYYY-MM-DD).
     */
    @Schema(description = "Date of birth (YYYY-MM-DD)", example = "1998-05-12")
    private LocalDate dateOfBirth;

    /**
     * Passenger nationality.
     */
    @Schema(description = "User nationality", example = "Indian")
    private String nationality;

    /**
     * Plaintext password (minimum 6 characters) to be BCrypt encrypted.
     */
    @Schema(description = "Account password (min 6 characters)", example = "Asha@12345")
    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 100, message = "Password must be at least 6 characters")
    private String password;

    /**
     * Default constructor for JSON deserialization.
     */
    public RegisterRequest() {
    }

    /**
     * Parameterized constructor.
     *
     * @param firstName user first name
     * @param lastName user last name
     * @param email account email
     * @param phoneNumber contact number
     * @param dateOfBirth birth date
     * @param nationality country
     * @param password plaintext password
     */
    public RegisterRequest(String firstName,
            String lastName,
            String email,
            String phoneNumber,
            LocalDate dateOfBirth,
            String nationality,
            String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.dateOfBirth = dateOfBirth;
        this.nationality = nationality;
        this.password = password;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
