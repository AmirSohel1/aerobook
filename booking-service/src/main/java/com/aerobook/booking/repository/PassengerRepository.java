package com.aerobook.booking.repository;

import com.aerobook.booking.entity.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * ============================================================================
 * Booking Passenger Spring Data JPA Repository
 * ============================================================================
 *
 * Data access abstraction managing individual passenger records associated with
 * ticket reservations in the {@code passengers} table.
 *
 * @author Aerobook Platform Engineering
 * @version 1.0.0
 */
@Repository
public interface PassengerRepository extends JpaRepository<Passenger, Long> {
}