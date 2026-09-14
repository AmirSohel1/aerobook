package com.aerobook.fare.dto.response;

import java.math.BigDecimal;

/**
 * ============================================================================
 * Flight Service Feign Response DTO
 * ============================================================================
 *
 * Minimal flight projection received from Flight Service during inter-service
 * validation and pricing synchronization.
 *
 * @author Aerobook Platform Engineering
 * @version 1.0.0
 */
public class FlightResponse {

    /**
     * Unique flight identifier.
     */
    private Long id;

    /**
     * Base flight operating fare in platform currency.
     */
    private BigDecimal baseFare;

    /**
     * Default constructor.
     */
    public FlightResponse() {
    }

    /**
     * Parameterized constructor.
     *
     * @param id flight identifier
     * @param baseFare base operating fare
     */
    public FlightResponse(Long id, BigDecimal baseFare) {
        this.id = id;
        this.baseFare = baseFare;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getBaseFare() {
        return baseFare;
    }

    public void setBaseFare(BigDecimal baseFare) {
        this.baseFare = baseFare;
    }
}
