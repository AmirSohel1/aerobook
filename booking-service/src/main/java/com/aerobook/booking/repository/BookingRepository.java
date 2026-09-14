package com.aerobook.booking.repository;

import com.aerobook.booking.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * ============================================================================
 * Flight Booking Spring Data JPA Repository
 * ============================================================================
 *
 * Provides persistence and query methods for airline ticket reservations
 * in the {@code bookings} table.
 *
 * @author Aerobook Platform Engineering
 * @version 1.0.0
 */
@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    /**
     * Resolves a booking reservation by its unique Passenger Name Record (PNR) code.
     *
     * @param pnr alphanumeric PNR string
     * @return an {@link Optional} containing the matched {@link Booking}, or empty
     */
    Optional<Booking> findByPnr(String pnr);

    /**
     * Retrieves all reservations created by a specific user account.
     *
     * @param userId customer user identifier
     * @return list of matching {@link Booking} records
     */
    List<Booking> findByUserId(Long userId);
}