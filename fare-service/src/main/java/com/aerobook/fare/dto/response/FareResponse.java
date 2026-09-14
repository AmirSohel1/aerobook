package com.aerobook.fare.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * ============================================================================
 * Flight Fare Pricing Schedule Response DTO
 * ============================================================================
 *
 * Public projection of cabin pricing tiers, tax rates, and active status
 * consumed by booking services and flight search engines.
 *
 * @author Aerobook Platform Engineering
 * @version 1.0.0
 */
@Schema(description = "Flight fare details response")
public class FareResponse {

    /**
     * Unique system database identifier.
     */
    @Schema(description = "Fare record database ID", example = "1")
    private Long id;

    /**
     * Associated flight database ID.
     */
    @Schema(description = "Associated flight database ID", example = "1")
    private Long flightId;

    /**
     * Base price for Economy Class seating.
     */
    @Schema(description = "Economy class fare price in INR", example = "5500.0")
    private BigDecimal economyFare;

    /**
     * Base price for Business Class seating.
     */
    @Schema(description = "Business class fare price in INR", example = "12500.0")
    private BigDecimal businessFare;

    /**
     * Base price for First Class luxury seating.
     */
    @Schema(description = "First class fare price in INR", example = "25000.0")
    private BigDecimal firstClassFare;

    /**
     * Applicable tax percentage.
     */
    @Schema(description = "Tax percentage applicable", example = "5.0")
    private BigDecimal taxPercentage;

    /**
     * Applicable promotional discount percentage.
     */
    @Schema(description = "Promotional discount percentage", example = "0.0")
    private BigDecimal discountPercentage;

    /**
     * Calendar date when fare became effective.
     */
    @Schema(description = "Effective date of fare (YYYY-MM-DD)", example = "2026-10-01")
    private LocalDate effectiveDate;

    /**
     * Flag indicating if the pricing schedule is active.
     */
    @Schema(description = "Indicates whether fare is active", example = "true")
    private Boolean active;

    /**
     * Default no-argument constructor.
     */
    public FareResponse() {
    }

    /**
     * Full-parameter constructor.
     *
     * @param id primary ID
     * @param flightId flight ID
     * @param economyFare economy price
     * @param businessFare business price
     * @param firstClassFare first class price
     * @param taxPercentage tax percentage
     * @param discountPercentage discount percentage
     * @param effectiveDate effective date
     * @param active active status
     */
    public FareResponse(Long id,
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
