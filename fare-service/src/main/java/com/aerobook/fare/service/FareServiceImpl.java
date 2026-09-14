package com.aerobook.fare.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aerobook.fare.dto.request.FareRequest;
import com.aerobook.fare.dto.response.FareResponse;
import com.aerobook.fare.entity.Fare;
import com.aerobook.fare.exception.ResourceNotFoundException;
import com.aerobook.fare.repository.FareRepository;

/**
 * ============================================================================
 * Fare & Revenue Management Business Service Implementation
 * ============================================================================
 *
 * Implements transactional business rules for managing tiered flight fares,
 * validating tax and discount limits, and translating domain models to DTOs.
 *
 * @author Aerobook Platform Engineering
 * @version 1.0.0
 */
@Service
@Transactional
public class FareServiceImpl implements FareService {

    private static final Logger log = LoggerFactory.getLogger(FareServiceImpl.class);

    private final FareRepository fareRepository;

    /**
     * Constructor injection for required database repository.
     *
     * @param fareRepository data access repository
     */
    public FareServiceImpl(FareRepository fareRepository) {
        this.fareRepository = fareRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FareResponse createFare(FareRequest request) {
        // 1. Enforce business validation rules on price and percentage ranges
        validateFareBusinessRules(request);

        // 2. Map request properties into a new Fare entity
        Fare fare = new Fare();
        fare.setFlightId(request.getFlightId());
        fare.setEconomyFare(request.getEconomyFare());
        fare.setBusinessFare(request.getBusinessFare());
        fare.setFirstClassFare(request.getFirstClassFare());
        fare.setTaxPercentage(request.getTaxPercentage());
        fare.setDiscountPercentage(request.getDiscountPercentage() != null ? request.getDiscountPercentage() : BigDecimal.ZERO);
        fare.setEffectiveDate(request.getEffectiveDate());
        fare.setActive(true);

        // 3. Persist and return mapped response
        Fare savedFare = fareRepository.save(fare);
        log.info("Created fare structure with ID: {} for flight ID: {}", savedFare.getId(), savedFare.getFlightId());
        return mapToResponse(savedFare);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FareResponse updateFare(Long id, FareRequest request) {
        // 1. Enforce price and percentage boundaries
        validateFareBusinessRules(request);

        // 2. Locate existing record or fail with 404
        Fare fare = fareRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fare not found with id: " + id));

        // 3. Update coordinates
        fare.setFlightId(request.getFlightId());
        fare.setEconomyFare(request.getEconomyFare());
        fare.setBusinessFare(request.getBusinessFare());
        fare.setFirstClassFare(request.getFirstClassFare());
        fare.setTaxPercentage(request.getTaxPercentage());
        fare.setDiscountPercentage(request.getDiscountPercentage() != null ? request.getDiscountPercentage() : BigDecimal.ZERO);
        fare.setEffectiveDate(request.getEffectiveDate());

        // 4. Save and return updated projection
        Fare updatedFare = fareRepository.save(fare);
        log.info("Updated fare structure with ID: {} for flight ID: {}", updatedFare.getId(), updatedFare.getFlightId());
        return mapToResponse(updatedFare);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public FareResponse getFareById(Long id) {
        log.debug("Retrieving fare structure with ID: {}", id);
        Fare fare = fareRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fare not found with id: " + id));
        return mapToResponse(fare);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public FareResponse getFareByFlightId(Long flightId) {
        log.debug("Retrieving fare structure for Flight ID: {}", flightId);
        Fare fare = fareRepository.findByFlightId(flightId)
                .orElseThrow(() -> new ResourceNotFoundException("Fare not found for flight id: " + flightId));
        return mapToResponse(fare);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<FareResponse> getAllFares() {
        return fareRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteFare(Long id) {
        Fare fare = fareRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fare not found with id: " + id));

        fareRepository.delete(fare);
        log.info("Deleted fare structure with ID: {}", id);
    }

    /**
     * Validates that fare amounts are non-negative and percentages fall in
     * [0.0, 100.0].
     *
     * @param request the incoming fare coordinates
     * @throws IllegalArgumentException if any business rule is violated
     */
    private void validateFareBusinessRules(FareRequest request) {
        if (request.getEconomyFare() != null && request.getEconomyFare().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Economy fare cannot be negative.");
        }
        if (request.getBusinessFare() != null && request.getBusinessFare().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Business fare cannot be negative.");
        }
        if (request.getFirstClassFare() != null && request.getFirstClassFare().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("First class fare cannot be negative.");
        }
        if (request.getTaxPercentage() != null && (request.getTaxPercentage().compareTo(BigDecimal.ZERO) < 0 || request.getTaxPercentage().compareTo(BigDecimal.valueOf(100)) > 0)) {
            throw new IllegalArgumentException("Tax percentage must be between 0.0% and 100.0%.");
        }
        if (request.getDiscountPercentage() != null && (request.getDiscountPercentage().compareTo(BigDecimal.ZERO) < 0 || request.getDiscountPercentage().compareTo(BigDecimal.valueOf(100)) > 0)) {
            throw new IllegalArgumentException("Discount percentage must be between 0.0% and 100.0%.");
        }
    }

    /**
     * Converts a database {@link Fare} entity to client-facing
     * {@link FareResponse} DTO.
     *
     * @param fare domain model
     * @return transformed DTO
     */
    private FareResponse mapToResponse(Fare fare) {
        FareResponse response = new FareResponse();
        response.setId(fare.getId());
        response.setFlightId(fare.getFlightId());
        response.setEconomyFare(fare.getEconomyFare());
        response.setBusinessFare(fare.getBusinessFare());
        response.setFirstClassFare(fare.getFirstClassFare());
        response.setTaxPercentage(fare.getTaxPercentage());
        response.setDiscountPercentage(fare.getDiscountPercentage());
        response.setEffectiveDate(fare.getEffectiveDate());
        response.setActive(fare.getActive());
        return response;
    }
}
