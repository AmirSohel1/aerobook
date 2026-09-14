package com.aerobook.booking.entity.enums;

/**
 * ============================================================================
 * Flight Booking Reservation Status Enumeration
 * ============================================================================
 *
 * Represents the lifecycle state of a passenger flight reservation.
 *
 * <ul>
 * <li>{@link #BOOKED}: Active, confirmed seat reservation</li>
 * <li>{@link #CANCELLED}: Annulled booking; seats released</li>
 * <li>{@link #PENDING}: Reservation awaiting payment or event confirmation</li>
 * </ul>
 *
 * @author Aerobook Platform Engineering
 * @version 1.0.0
 */
public enum BookingStatus {
    /**
     * Confirmed booking with seats secured.
     */
    BOOKED,
    /**
     * Cancelled booking with seats released back to fleet inventory.
     */
    CANCELLED,
    /**
     * Transient reservation state prior to final confirmation.
     */
    PENDING
}
