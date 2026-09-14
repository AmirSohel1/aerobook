package com.aerobook.apigateway;

import com.aerobook.apigateway.dto.ErrorResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * ============================================================================
 * Gateway Application Core Unit Tests
 * ============================================================================
 *
 * Verifies core model instantiation, error response payload mapping, and
 * gateway component integrity without requiring live network sockets or
 * database connections.
 *
 * @author Aerobook Platform Engineering
 */
class ApiGatewayApplicationTests {

    /**
     * Verifies that the standard ErrorResponse model properly retains
     * timestamp, status code, error phrase, and explanation message.
     */
    @Test
    @DisplayName("ErrorResponse DTO correctly captures error fields and getters")
    void errorResponseModelInstantiatesCorrectly() {
        LocalDateTime now = LocalDateTime.now();
        ErrorResponse error = new ErrorResponse(
                now,
                401,
                "Unauthorized",
                "Missing or invalid JWT token"
        );

        assertThat(error.getTimestamp()).isEqualTo(now);
        assertThat(error.getStatus()).isEqualTo(401);
        assertThat(error.getError()).isEqualTo("Unauthorized");
        assertThat(error.getMessage()).isEqualTo("Missing or invalid JWT token");
    }
}
