package com.aerobook.booking.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * ============================================================================
 * Flight Booking Passenger JPA Entity
 * ============================================================================
 *
 * Persists individual passenger demographics, identity names, age, gender,
 * and allocated seat assignment linked to a parent {@link Booking} entity.
 * Maps to the {@code passengers} table in {@code aerobook_booking_db}.
 *
 * @author Aerobook Platform Engineering
 * @version 1.0.0
 */
@Entity
@Table(name = "passengers")
public class Passenger {

    /**
     * Unique auto-increment primary identifier.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long passengerId;

    /**
     * Passenger legal first name.
     */
    @Column(nullable = false, length = 50)
    private String firstName;

    /**
     * Passenger legal last surname.
     */
    @Column(nullable = false, length = 50)
    private String lastName;

    /**
     * Age in completed years.
     */
    @Column(nullable = false)
    private Integer age;

    /**
     * Gender designation (Male, Female, Other).
     */
    @Column(nullable = false, length = 10)
    private String gender;

    /**
     * Assigned physical seat identifier (e.g., "14A").
     */
    @Column(length = 10)
    private String seatNumber;

    /**
     * Foreign key relationship linking back to the parent reservation.
     */
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    /**
     * Default no-argument constructor required by JPA.
     */
    public Passenger() {
    }

    /**
     * Full-parameter constructor.
     *
     * @param passengerId passenger primary ID
     * @param firstName passenger first name
     * @param lastName passenger last name
     * @param age passenger age
     * @param gender passenger gender
     * @param seatNumber seat number
     * @param booking parent booking reference
     */
    public Passenger(Long passengerId, String firstName, String lastName,
            Integer age, String gender, String seatNumber, Booking booking) {
        this.passengerId = passengerId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.gender = gender;
        this.seatNumber = seatNumber;
        this.booking = booking;
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

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }
}
