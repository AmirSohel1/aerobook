package com.aerobook.flight.event;

/**
 * Event published when reserved seats are restored back to inventory upon
 * booking cancellation.
 */
public class SeatReleasedEvent {

    /**
     * Unique identifier of the flight whose seats were released.
     */
    private Long flightId;

    /**
     * Unique identifier of the cancelled reservation.
     */
    private Long bookingId;

    /**
     * Current count of available seats following the release.
     */
    private Integer availableSeats;

    /**
     * Default no-args constructor for JSON deserialization.
     */
    public SeatReleasedEvent() {
    }

    /**
     * Constructs a {@link SeatReleasedEvent} with flight, booking, and
     * inventory details.
     *
     * @param flightId flight database ID
     * @param bookingId booking database ID
     * @param availableSeats updated seat count
     */
    public SeatReleasedEvent(Long flightId,
            Long bookingId,
            Integer availableSeats) {

        this.flightId = flightId;
        this.bookingId = bookingId;
        this.availableSeats = availableSeats;
    }

    public Long getFlightId() {
        return flightId;
    }

    public void setFlightId(Long flightId) {
        this.flightId = flightId;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public Integer getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(Integer availableSeats) {
        this.availableSeats = availableSeats;
    }

    @Override
    public String toString() {
        return "SeatReleasedEvent [flightId=" + flightId
                + ", bookingId=" + bookingId
                + ", availableSeats=" + availableSeats + "]";
    }
}
