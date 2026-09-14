package com.aerobook.fare.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * ============================================================================
 * Flight Fare Pricing Structure JPA Entity
 * ============================================================================
 *
 * Persists cabin pricing tiers, applicable taxes, promotional discounts,
 * and active status for a specific commercial flight schedule.
 * Maps to the {@code fares} table in {@code aerobook_fare_db}.
 *
 * @author Aerobook Platform Engineering
 * @version 1.0.0
 */
@Entity
@Table(name = "fares")
public class Fare {

    /**
     * Unique auto-increment primary identifier for the fare record.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Foreign flight identifier establishing a 1-to-1 relationship with Flight Service.
     */
    @Column(name = "flight_id", nullable = false, unique = true)
    private Long flightId;

    /**
     * Base price for standard Economy Class seating (in platform currency, INR).
     */
    @Column(name = "economy_fare", nullable = false)
    private BigDecimal economyFare;

    /**
     * Base price for premium Business Class seating.
     */
    @Column(name = "business_fare")
    private BigDecimal businessFare;

    /**
     * Base price for First Class luxury seating.
     */
    @Column(name = "first_class_fare")
    private BigDecimal firstClassFare;

    /**
     * Government or airport statutory tax percentage (e.g. 18.00 for 18% GST).
     */
    @Column(name = "tax_percentage")
    private BigDecimal taxPercentage;

    /**
     * Promotional discount percentage deducted before tax assessment.
     */
    @Column(name = "discount_percentage")
    private BigDecimal discountPercentage;

    /**
     * Date from which this pricing structure becomes active.
     */
    @Column(name = "effective_date")
    private LocalDate effectiveDate;

    /**
     * Flag indicating whether this fare configuration is currently active.
     */
    @Column(name = "active")
    private Boolean active = true;

    /**
     * Default no-argument constructor required by JPA.
     */
    public Fare() {
    }

    /**
     * Full-parameter constructor.
     *
     * @param id primary ID
     * @param flightId linked flight ID
     * @param economyFare economy base price
     * @param businessFare business base price
     * @param firstClassFare first class base price
     * @param taxPercentage tax percentage
     * @param discountPercentage discount percentage
     * @param effectiveDate effective date
     * @param active active flag
     */
    public Fare(Long id,
                Long flightId,
                BigDecimal economyFare,
                BigDecimal businessFare,
                BigDecimal firstClassFare,
                BigDecimal taxPercentage,
                BigDecimal discountPercentage,
                LocalDate effectiveDate,
                Boolean active) {
        this.id = id;
        this.flightId = flightId;
        this.economyFare = economyFare;
        this.businessFare = businessFare;
        this.firstClassFare = firstClassFare;
        this.taxPercentage = taxPercentage;
        this.discountPercentage = discountPercentage;
        this.effectiveDate = effectiveDate;
        this.active = active;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFlightId() {
        return flightId;
    }

    public void setFlightId(Long flightId) {
        this.flightId = flightId;
    }

    public BigDecimal getEconomyFare() {
        return economyFare;
    }

    public void setEconomyFare(BigDecimal economyFare) {
        this.economyFare = economyFare;
    }

    public BigDecimal getBusinessFare() {
        return businessFare;
    }

    public void setBusinessFare(BigDecimal businessFare) {
        this.businessFare = businessFare;
    }

    public BigDecimal getFirstClassFare() {
        return firstClassFare;
    }

    public void setFirstClassFare(BigDecimal firstClassFare) {
        this.firstClassFare = firstClassFare;
    }

    public BigDecimal getTaxPercentage() {
        return taxPercentage;
    }

    public void setTaxPercentage(BigDecimal taxPercentage) {
        this.taxPercentage = taxPercentage;
    }

    public BigDecimal getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(BigDecimal discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public LocalDate getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(LocalDate effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}