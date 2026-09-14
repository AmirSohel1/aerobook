package com.aerobook.flight.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aerobook.flight.entity.Flight;

/**
 * Repository interface for Flight entity.
 *
 * Provides database operations related to flight management.
 */
@Repository
public interface FlightRepository extends JpaRepository<Flight, Long> {

    /**
     * Finds a flight using flight number.
     *
     * @param flightNumber unique flight number
     * @return Optional containing flight details
     */
    Optional<Flight> findByFlightNumber(String flightNumber);

    /**
     * Finds flights based on source and destination.
     *
     * @param source departure location
     * @param destination arrival location
     * @return list of matching flights
     */
    List<Flight> findBySourceAndDestination(
            String source,
            String destination);

    /**
     * Finds flights between source and destination
     * within a specified departure time range.
     *
     * @param source departure location
     * @param destination arrival location
     * @param start start date and time
     * @param end end date and time
     * @return list of matching flights
     */
    List<Flight> findBySourceAndDestinationAndDepartureTimeBetween(
            String source,
            String destination,
            LocalDateTime start,
            LocalDateTime end);

}