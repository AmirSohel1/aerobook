package com.aerobook.booking.service;

import com.aerobook.booking.dto.BookingRequest;
import com.aerobook.booking.dto.BookingResponse;

import java.util.List;

/**
 * Service interface managing flight booking lifecycle, business validations,
 * PNR assignments, and reservation queries.
 */
public interface BookingService {

    /**
     * Creates and confirms a new flight reservation with comprehensive business
     * logic: - Validates passenger list presence and structure - Verifies
     * flight existence via OpenFeign - Enforces flight schedule (rejects
     * departed flights) - Enforces flight status (rejects CANCELLED or
     * COMPLETED flights) - Verifies seat availability against the party size -
     * Generates unique PNR, computes total fare, stores booking, and dispatches
     * event.
     *
     * @param request the reservation parameters
     * @return the confirmed booking response
     */
    BookingResponse createBooking(BookingRequest request);

    /**
     * Finds a booking by its primary database ID.
     *
     * @param bookingId booking ID
     * @return booking response
     */
    BookingResponse getBookingById(Long bookingId);

    /**
     * Finds a booking by its unique Passenger Name Record (PNR).
     *
     * @param pnr passenger name record code
     * @return booking response
     */
    BookingResponse getBookingByPnr(String pnr);

    /**
     * Lists all bookings associated with a specific customer account.
     *
     * @param userId user account ID
     * @return list of bookings
     */
    List<BookingResponse> getBookingsByUserId(Long userId);

    /**
     * Lists all bookings across the entire airline system (Admin operation).
     *
     * @return list of all bookings
     */
    List<BookingResponse> getAllBookings();

    /**
     * Cancels an existing booking. Safeguards against double cancellation and
     * invalid IDs.
     *
     * @param bookingId booking ID to cancel
     * @return confirmation message
     */
    String cancelBooking(Long bookingId);
}
