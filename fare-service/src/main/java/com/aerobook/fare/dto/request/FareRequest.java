package com.aerobook.fare.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * ============================================================================
 * Flight Fare Pricing Schedule Request DTO
 * ============================================================================
 *
 * Payload submitted by airline revenue administrators to establish or alter
 * tiered cabin pricing, GST tax percentages, and promotional discounts.
 *
 * @author Aerobook Platform Engineering
 * @version 1.0.0
 */
@Schema(description = "Request payload for creating or modifying flight fares")
public class FareRequest {

    /**
     * Unique target flight schedule database ID.
     */
    @Schema(description = "Associated flight database ID", example = "1")
    @NotNull(message = "Flight ID is required")
    private Long flightId;

    /**
     * Base price for standard Economy Class seating (INR).
     */
    @Schema(description = "Economy class fare price in INR", example = "4500.00")
    @NotNull(message = "Economy fare is required")
    @DecimalMin(value = "0.0", message = "Economy fare cannot be negative")
    private BigDecimal economyFare;

    /**
     * Base price for Business Class seating (INR).
     */
    @Schema(description = "Business class fare price in INR", example = "8500.00")
    @NotNull(message = "Business fare is required")
    @DecimalMin(value = "0.0", message = "Business fare cannot be negative")
    private BigDecimal businessFare;

    /**
     * Base price for First Class luxury seating (INR).
     */
    @Schema(description = "First class fare price in INR", example = "14000.00")
    @NotNull(message = "First class fare is required")
    @DecimalMin(value = "0.0", message = "First class fare cannot be negative")
    private BigDecimal firstClassFare;

    /**
     * Statutory GST / airport tax rate percentage (0.0 to 100.0).
     */
    @Schema(description = "Tax percentage applicable (0-100)", example = "18.0")
    @NotNull(message = "Tax percentage is required")
    @DecimalMin(value = "0.0", message = "Tax percentage cannot be negative")
    @DecimalMax(value = "100.0", message = "Tax percentage cannot exceed 100%")
    private BigDecimal taxPercentage;

    /**
     * Promotional markdown percentage applied before taxation (0.0 to 100.0).
     */
    @Schema(description = "Promotional discount percentage (0-100)", example = "5.0")
    @DecimalMin(value = "0.0", message = "Discount percentage cannot be negative")
    @DecimalMax(value = "100.0", message = "Discount percentage cannot exceed 100%")
    private BigDecimal discountPercentage;

    /**
     * Calendar date from which the fare structure comes into effect.
     */
    @Schema(description = "Effective date of fare (YYYY-MM-DD)", example = "2026-10-01")
    private LocalDate effectiveDate;

    /**
     * Default no-argument constructor.
     */
    public FareRequest() {
    }

    /**
     * Full-parameter constructor for request instantiation.
     *
     * @param flightId flight ID
     * @param economyFare economy base price
     * @param businessFare business base price
     * @param firstClassFare first class base price
     * @param taxPercentage tax percentage
     * @param discountPercentage discount percentage
     * @param effectiveDate effective date
     */
    public FareRequest(Long flightId,
            BigDecimal economyFare,
            BigDecimal businessFare,
            BigDecimal firstClassFare,
            BigDecimal taxPercentage,
            BigDecimal discountPercentage,
            LocalDate effectiveDate) {
        this.flightId = flightId;
        this.economyFare = economyFare;
        this.businessFare = businessFare;
        this.firstClassFare = firstClassFare;
        this.taxPercentage = taxPercentage;
        this.discountPercentage = discountPercentage;
        this.effectiveDate = effectiveDate;
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
}
