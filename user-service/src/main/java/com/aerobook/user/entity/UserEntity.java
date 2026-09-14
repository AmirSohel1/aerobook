package com.aerobook.user.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

/**
 * ============================================================================
 * User Profile JPA Entity
 * ============================================================================
 *
 * Persisted entity mapping to the {@code users} table in
 * {@code aerobook_user_db}. Stores customer profile demographics, contact
 * coordinates, security roles, and automated lifecycle audit timestamps.
 *
 * @author Aerobook Platform Engineering
 * @version 1.0.0
 */
@Entity
@Table(name = "users")
public class UserEntity {

    /**
     * Unique auto-increment primary key identifying the user account.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    /**
     * Customer first given name (max 50 characters).
     */
    @Column(nullable = false, length = 50)
    private String firstName;

    /**
     * Customer last family surname (max 50 characters).
     */
    @Column(nullable = false, length = 50)
    private String lastName;

    /**
     * Customer unique electronic mail address used across platform
     * authentication.
     */
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    /**
     * Customer unique telephone / mobile contact number (max 15 characters).
     */
    @Column(nullable = false, unique = true, length = 15)
    private String phoneNumber;

    /**
     * Passenger birth date for identity and age verification.
     */
    private LocalDate dateOfBirth;

    /**
     * Country of citizenship or nationality.
     */
    private String nationality;

    /**
     * Assigned platform security authority role (defaults to
     * {@link Role#ROLE_USER}).
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.ROLE_USER;

    /**
     * Audit timestamp when the profile record was originally created.
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Audit timestamp when the profile record was last modified.
     */
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Default no-args constructor required by JPA specification.
     */
    public UserEntity() {
    }

    /**
     * Parameterized constructor initializing complete user profile fields.
     *
     * @param userId unique user ID
     * @param firstName customer first name
     * @param lastName customer last name
     * @param email customer email
     * @param phoneNumber contact phone number
     * @param dateOfBirth date of birth
     * @param nationality nationality
     * @param role security authority role
     */
    public UserEntity(Long userId, String firstName, String lastName, String email,
            String phoneNumber, LocalDate dateOfBirth, String nationality, Role role) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.dateOfBirth = dateOfBirth;
        this.nationality = nationality;
        this.role = role == null ? Role.ROLE_USER : role;
    }

    /**
     * Lifecycle callback establishing initial creation and update timestamps
     * prior to database insertion.
     */
    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.role == null) {
            this.role = Role.ROLE_USER;
        }
    }

    /**
     * Lifecycle callback updating the modification timestamp before updates.
     */
    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
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

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role == null ? Role.ROLE_USER : role;
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
}
