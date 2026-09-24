package com.aerobook.checkin.service.impl;

import com.aerobook.checkin.dto.CheckInRequest;
import com.aerobook.checkin.entity.CheckIn;
import com.aerobook.checkin.repository.CheckInRepository;
import com.aerobook.checkin.service.CheckInService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

/**
 * ============================================================================
 * Check-In Business Service Implementation
 * ============================================================================
 *
 * Implements business operations for airport check-in, seat confirmation,
 * barcode generation, and departure gate verifications.
 *
 * @author Aerobook Platform Engineering
 * @version 1.0.0
 */
@Service
public class CheckInServiceImpl implements CheckInService {

    private static final Logger log = LoggerFactory.getLogger(CheckInServiceImpl.class);

    private final CheckInRepository checkInRepository;

    public CheckInServiceImpl(CheckInRepository checkInRepository) {
        this.checkInRepository = checkInRepository;
    }

    @Override
    public CheckIn performCheckIn(CheckInRequest request) {
        log.info("Processing check-in for booking ID: {}, passenger: {}", request.getBookingId(), request.getPassengerName());

        // 1. Idempotency: re-checking in the same booking updates the existing record
        CheckIn checkIn = checkInRepository.findByBookingId(request.getBookingId())
                .orElseGet(CheckIn::new);

        // 2. Normalize requested seat or default to 12A
        String seat = (request.getSeatNumber() != null && !request.getSeatNumber().isBlank())
                ? request.getSeatNumber().toUpperCase()
                : "12A";

        // 3. Assign coordinates and generate boarding pass
        checkIn.setBookingId(request.getBookingId());
        checkIn.setPassengerName(request.getPassengerName());
        checkIn.setSeatNumber(seat);
        checkIn.setBoardingPassNumber("BP-" + request.getBookingId() + "-" + seat);
        checkIn.setStatus("CHECKED_IN");
        checkIn.setCheckedInAt(LocalDateTime.now());

        CheckIn saved = checkInRepository.save(checkIn);
        log.info("Passenger {} checked in successfully with boarding pass: {}", saved.getPassengerName(), saved.getBoardingPassNumber());
        return saved;
    }

    @Override
    public CheckIn getCheckInByBookingId(Long bookingId) {
        return checkInRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Check-in not found for booking ID: " + bookingId));
    }

    @Override
    public CheckIn getCheckInById(Long id) {
        return checkInRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Check-in record not found with ID: " + id));
    }

    @Override
    public List<CheckIn> getAllCheckIns() {
        return checkInRepository.findAll();
    }

    @Override
    public CheckIn verifyBoardingPass(String boardingPassNumber) {
        log.info("Gate staff verifying boarding pass: {}", boardingPassNumber);

        CheckIn checkIn = checkInRepository.findByBoardingPassNumber(boardingPassNumber.trim().toUpperCase())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Boarding pass not found: " + boardingPassNumber));

        checkIn.setStatus("BOARDED");
        CheckIn saved = checkInRepository.save(checkIn);
        log.info("Boarding pass {} verified. Status updated to BOARDED.", boardingPassNumber);
        return saved;
    }
}
