package com.aerobook.auth.dto;

import java.time.LocalDate;

/**
 * ============================================================================
 * User Service Profile Creation Request Data Transfer Object
 * ============================================================================
 *
 * Payload transmitted by Auth Service to User Service via OpenFeign
 * ({@link com.aerobook.auth.client.UserClient}) to create customer profiles.
 *
 * @author Aerobook Security Engineering
 * @version 1.0.0
 */
public class UserCreateRequest {

    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private LocalDate dateOfBirth;
    private String nationality;
    private String role;

    public UserCreateRequest() {
    }

    public UserCreateRequest(RegisterRequest request) {
        this(request, "ROLE_USER");
    }

    public UserCreateRequest(RegisterRequest request, String role) {
        this.firstName = request.getFirstName();
        this.lastName = request.getLastName();
        this.email = request.getEmail();
        this.phoneNumber = request.getPhoneNumber();
        this.dateOfBirth = request.getDateOfBirth();
        this.nationality = request.getNationality();
        this.role = role;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public String getNationality() {
        return nationality;
    }

    public String getRole() {
        return role;
    }
}
