package com.aerobook.checkin.repository;

import com.aerobook.checkin.entity.CheckIn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * ============================================================================
 * Check-In Spring Data JPA Repository
 * ============================================================================
 *
 * Data access abstraction managing airport check-in records and booking lookups
 * in the {@code check_in} table of {@code aerobook_checkin_db}.
 *
 * @author Aerobook Platform Engineering
 * @version 1.0.0
 */
@Repository
public interface CheckInRepository extends JpaRepository<CheckIn, Long> {

    /**
     * Looks up an existing check-in confirmation by its associated booking identifier.
     * Prevents duplicate check-in submissions for the same ticket reservation.
     *
     * @param bookingId unique booking identifier
     * @return an {@link Optional} containing the matched {@link CheckIn} entity, or empty
     */
    Optional<CheckIn> findByBookingId(Long bookingId);
}
