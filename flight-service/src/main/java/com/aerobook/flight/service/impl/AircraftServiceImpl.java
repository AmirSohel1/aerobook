package com.aerobook.flight.service.impl;

import java.util.List;
import org.springframework.stereotype.Service;
import com.aerobook.flight.dto.request.CreateAircraftRequest;
import com.aerobook.flight.entity.Aircraft;
import com.aerobook.flight.enums.AircraftStatus;
import com.aerobook.flight.exception.AircraftNotFoundException;
import com.aerobook.flight.repository.AircraftRepository;
import com.aerobook.flight.service.AircraftService;

/**
 * * Implementation of {@link AircraftService} providing business operations for
 * * fleet management.
 */
@Service
public class AircraftServiceImpl implements AircraftService {

    /**
     * * JPA repository for persisting aircraft records.
     */
    private final AircraftRepository aircraftRepository;

    /**
     * * Constructs an {@link AircraftServiceImpl} with the given repository. *
     * * @param aircraftRepository aircraft JPA repository
     */
    public AircraftServiceImpl(AircraftRepository aircraftRepository) {
        this.aircraftRepository = aircraftRepository;
    }

    @Override
    public Aircraft createAircraft(CreateAircraftRequest request) {
        Aircraft aircraft = new Aircraft();
        aircraft.setAircraftCode(request.getAircraftCode());
        aircraft.setAircraftName(request.getAircraftName());
        aircraft.setManufacturer(request.getManufacturer());
        aircraft.setCapacity(request.getCapacity());
        aircraft.setStatus(AircraftStatus.ACTIVE);
        return aircraftRepository.save(aircraft);
    }

    @Override
    public Aircraft getAircraft(Long id) {
        return aircraftRepository.findById(id).orElseThrow(() -> new AircraftNotFoundException("Aircraft not found with id : " + id));
    }

    @Override
    public List<Aircraft> getAllAircrafts() {
        return aircraftRepository.findAll();
    }

    @Override
    public void deleteAircraft(Long id) {
        aircraftRepository.deleteById(id);
    }
}
