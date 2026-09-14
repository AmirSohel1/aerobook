package com.aerobook.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;

/**
 * ============================================================================
 * User Profile Details Response DTO
 * ============================================================================
 *
 * Public projection of customer account coordinates returned to frontend
 * consumers and downstream microservices. Omits sensitive internals.
 *
 * @author Aerobook Platform Engineering
 * @version 1.0.0
 */
@Schema(description = "User profile details response")
public class UserResponse {

    /**
     * Unique system database identifier.
     */
    @Schema(description = "Unique user database identifier", example = "1")
    private Long userId;

    /**
     * Customer first given name.
     */
    @Schema(description = "User's first name", example = "Ravi")
    private String firstName;

    /**
     * Customer last family surname.
     */
    @Schema(description = "User's last name", example = "Patel")
    private String lastName;

    /**
     * Primary registered contact electronic mail address.
     */
    @Schema(description = "Registered email address", example = "ravi.patel@example.com")
    private String email;

    /**
     * Customer registered telephone contact number.
     */
    @Schema(description = "Contact phone number", example = "9876543212")
    private String phoneNumber;

    /**
     * Date of birth (YYYY-MM-DD).
     */
    @Schema(description = "Date of birth (YYYY-MM-DD)", example = "1995-09-20")
    private LocalDate dateOfBirth;

    /**
     * Passenger country of citizenship or nationality.
     */
    @Schema(description = "Nationality of the customer", example = "Indian")
    private String nationality;

    /**
     * Assigned security authorization authority (ROLE_USER or ROLE_ADMIN).
     */
    @Schema(description = "Security role assigned to account (ROLE_USER or ROLE_ADMIN)", example = "ROLE_USER")
    private String role;

    /**
     * Default no-argument constructor.
     */
    public UserResponse() {
    }

    /**
     * Full-parameter constructor.
     *
     * @param userId unique user identifier
     * @param firstName customer first name
     * @param lastName customer last name
     * @param email customer email
     * @param phoneNumber contact phone
     * @param dateOfBirth date of birth
     * @param nationality nationality
     * @param role security authority role
     */
    public UserResponse(Long userId, String firstName, String lastName,
            String email, String phoneNumber, LocalDate dateOfBirth,
            String nationality, String role) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.dateOfBirth = dateOfBirth;
        this.nationality = nationality;
        this.role = role;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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
