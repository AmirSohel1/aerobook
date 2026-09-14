package com.aerobook.flight.event;

/**
 * Event published when a new booking is created and confirmed. Consumed by
 * Flight Service to decrement available seat inventory.
 */
public class BookingCreatedEvent {

    /**
     * Unique database identifier of the confirmed booking.
     */
    private Long bookingId;

    /**
     * Unique database identifier of the booked flight.
     */
    private Long flightId;

    /**
     * Default no-args constructor for JSON deserialization.
     */
    public BookingCreatedEvent() {
    }

    /**
     * Constructs a {@link BookingCreatedEvent} with booking and flight IDs.
     *
     * @param bookingId database booking ID
     * @param flightId database flight ID
     */
    public BookingCreatedEvent(Long bookingId, Long flightId) {
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
        return "BookingCreatedEvent [bookingId=" + bookingId
                + ", flightId=" + flightId + "]";
    }
}
