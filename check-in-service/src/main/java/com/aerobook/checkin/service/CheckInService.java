package com.aerobook.checkin.service;

import com.aerobook.checkin.dto.CheckInRequest;
import com.aerobook.checkin.entity.CheckIn;

import java.util.List;

/**
 * ============================================================================
 * Check-In Business Service Interface
 * ============================================================================
 *
 * Defines business operations for passenger check-in, seat confirmation,
 * boarding pass generation, gate verification, and administrative audit.
 *
 * @author Aerobook Platform Engineering
 * @version 1.0.0
 */
public interface CheckInService {

    /**
     * Checks in a passenger, assigns seat, and generates boarding pass.
     *
     * @param request passenger check-in payload
     * @return saved check-in record
     */
    CheckIn performCheckIn(CheckInRequest request);

    /**
     * Finds check-in record by booking ID.
     *
     * @param bookingId booking ID
     * @return check-in record
     */
    CheckIn getCheckInByBookingId(Long bookingId);

    /**
     * Finds check-in record by primary ID.
     *
     * @param id check-in database ID
     * @return check-in record
     */
    CheckIn getCheckInById(Long id);

    /**
     * Retrieves all check-in records across the airline.
     *
     * @return list of all check-in records
     */
    List<CheckIn> getAllCheckIns();

    /**
     * Verifies boarding pass at departure gate and marks passenger as BOARDED.
     *
     * @param boardingPassNumber boarding pass alphanumeric code
     * @return updated check-in record
     */
    CheckIn verifyBoardingPass(String boardingPassNumber);
}
