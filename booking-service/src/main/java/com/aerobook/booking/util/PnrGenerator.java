package com.aerobook.booking.util;

import java.util.UUID;

/**
 * *
 * ============================================================================
 * * Passenger Name Record (PNR) Number Generator Utility *
 * ============================================================================
 * * * Generates unique, standard alphanumeric airline reservation PNR strings *
 * conforming to the format {@code PNR-<8-HEX-CHARS>} * (e.g.
 * {@code PNR-A1B2C3D4}). * * @author Aerobook Platform Engineering * @version
 * 1.0.0
 */
public final class PnrGenerator {

    /**
     * * Private constructor enforcing utility class design pattern.
     */
    private PnrGenerator() {
    }

    /**
     * * Generates a new randomized uppercase PNR reference code. * * @return
     * unique PNR string formatted as "PNR-XXXXXXXX"
     */
    public static String generatePnr() {
        return "PNR-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
