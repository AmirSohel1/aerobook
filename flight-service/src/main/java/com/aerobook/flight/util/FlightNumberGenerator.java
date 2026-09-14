package com.aerobook.flight.util;

import java.util.Random;

/**
 * Utility class for generating random commercial flight numbers (e.g. AI1024).
 */
public final class FlightNumberGenerator {

    /**
     * Random number generator instance.
     */
    private static final Random RANDOM = new Random();

    /**
     * Private constructor to prevent instantiation of utility class.
     */
    private FlightNumberGenerator() {
    }

    /**
     * Generates a unique commercial flight number combining the airline code
     * with a 4-digit random sequence.
     *
     * @param airlineCode two or three letter airline prefix (e.g., "AI", "6E")
     * @return generated flight number string
     */
    public static String generate(String airlineCode) {
        int number = 1000 + RANDOM.nextInt(9000);
        return airlineCode.toUpperCase() + number;
    }
}
