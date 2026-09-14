package com.aerobook.fare.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aerobook.fare.entity.Fare;

/**
 * ============================================================================
 * Fare Pricing Spring Data JPA Repository
 * ============================================================================
 *
 * Provides persistence and query routines for cabin pricing structures
 * associated with commercial flights.
 *
 * @author Aerobook Platform Engineering
 * @version 1.0.0
 */
@Repository
public interface FareRepository extends JpaRepository<Fare, Long> {

    /**
     * Looks up the active pricing structure configured for a specific flight
     * ID.
     *
     * @param flightId flight database identifier
     * @return an {@link Optional} containing the matched {@link Fare}, or empty
     */
    Optional<Fare> findByFlightId(Long flightId);

    /**
     * Determines whether a pricing schedule has already been configured for the
     * flight.
     *
     * @param flightId flight database identifier
     * @return {@code true} if fare configuration exists, {@code false}
     * otherwise
     */
    boolean existsByFlightId(Long flightId);
}
