package com.aerobook.flight.util;

import java.time.LocalDateTime;

/**
 * Utility class providing date and time validation methods for flight
 * scheduling.
 */
public final class DateTimeUtil {

    /**
     * Private constructor to prevent instantiation of utility class.
     */
    private DateTimeUtil() {
    }

    /**
     * Validates that the scheduled arrival timestamp occurs strictly after
     * departure.
     *
     * @param departure departure timestamp
     * @param arrival arrival timestamp
     * @return true if arrival is after departure; false otherwise
     */
    public static boolean isArrivalAfterDeparture(
            LocalDateTime departure,
            LocalDateTime arrival) {
        return arrival.isAfter(departure);
    }

    /**
     * Verifies whether a given timestamp is scheduled in the future.
     *
     * @param dateTime the timestamp to evaluate
     * @return true if timestamp is in the future; false otherwise
     */
    public static boolean isFutureDate(
            LocalDateTime dateTime) {
        return dateTime.isAfter(LocalDateTime.now());
    }
}
