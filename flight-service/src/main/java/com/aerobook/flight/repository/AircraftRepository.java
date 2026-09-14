package com.aerobook.flight.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aerobook.flight.entity.Aircraft;

/**
 * Repository interface for Aircraft entity.
 *
 * Provides database operations related to aircraft management.
 */
@Repository
public interface AircraftRepository extends JpaRepository<Aircraft, Long> {

    /**
     * Finds an aircraft by its unique aircraft code.
     *
     * @param aircraftCode unique aircraft code
     * @return Optional containing Aircraft if found
     */
    Optional<Aircraft> findByAircraftCode(String aircraftCode);

}