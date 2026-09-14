package com.aerobook.flight.event;

/**
 * Event published when a customer booking is cancelled. Consumed by Flight
 * Service to release reserved seats back to inventory.
 */
public class BookingCancelledEvent {

    /**
     * Unique database identifier of the cancelled booking.
     */
    private Long bookingId;

    /**
     * Unique database identifier of the associated flight.
     */
    private Long flightId;

    /**
     * Default no-args constructor for JSON deserialization.
     */
    public BookingCancelledEvent() {
    }

    /**
     * Constructs a {@link BookingCancelledEvent} with booking and flight IDs.
     *
     * @param bookingId database booking ID
     * @param flightId database flight ID
     */
    public BookingCancelledEvent(Long bookingId, Long flightId) {
        this.bookingId = bookingId;
        this.flightId = flightId;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public Long getFlightId() {
        return flightId;
    }

    public void setFlightId(Long flightId) {
        this.flightId = flightId;
    }

    @Override
    public String toString() {
        return "BookingCancelledEvent [bookingId=" + bookingId
                + ", flightId=" + flightId + "]";
    }
}
