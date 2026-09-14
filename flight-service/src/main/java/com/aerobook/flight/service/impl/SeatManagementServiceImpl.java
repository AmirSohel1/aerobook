package com.aerobook.flight.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.aerobook.flight.entity.Flight;
import com.aerobook.flight.repository.FlightRepository;
import com.aerobook.flight.service.SeatManagementService;

/**
 * Implementation of {@link SeatManagementService} managing atomic seat
 * reservation and release operations on scheduled flights.
 */
@Service
public class SeatManagementServiceImpl
        implements SeatManagementService {

    private static final Logger log = LoggerFactory.getLogger(SeatManagementServiceImpl.class);

    /**
     * JPA repository for persisting flight availability state changes.
     */
    private final FlightRepository flightRepository;

    /**
     * Constructs a new {@link SeatManagementServiceImpl} with the flight
     * repository.
     *
     * @param flightRepository flight JPA repository
     */
    public SeatManagementServiceImpl(
            FlightRepository flightRepository) {

        this.flightRepository = flightRepository;
    }

    @Override
    public void reserveSeat(Long flightId) {

        Flight flight = flightRepository.findById(flightId)
                .orElseThrow(()
                        -> new RuntimeException("Flight not found"));

        if (flight.getAvailableSeats() <= 0) {
            log.warn("Cannot reserve seat: Flight ID {} has no available seats!", flightId);
            throw new RuntimeException("No seats available");
        }

        flight.setAvailableSeats(
                flight.getAvailableSeats() - 1);

        flightRepository.save(flight);
        log.info("Reserved 1 seat on Flight ID: {}. Remaining available seats: {}", flightId, flight.getAvailableSeats());
    }

    @Override
    public void releaseSeat(Long flightId) {

        Flight flight = flightRepository.findById(flightId)
                .orElseThrow(()
                        -> new RuntimeException("Flight not found"));

        flight.setAvailableSeats(
                flight.getAvailableSeats() + 1);

        flightRepository.save(flight);
        log.info("Released 1 seat on Flight ID: {}. Updated available seats: {}", flightId, flight.getAvailableSeats());
    }
}
