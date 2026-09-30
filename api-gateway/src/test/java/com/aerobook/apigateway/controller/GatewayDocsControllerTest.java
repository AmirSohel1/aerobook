package com.aerobook.apigateway.controller;

import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * ============================================================================
 * Gateway Docs Controller Unit Tests (Swagger UI Dropdown Engine)
 * ============================================================================
 *
 * Verifies that {@link GatewayDocsController} correctly serves isolated,
 * service-scoped OpenAPI 3 specifications for each microservice dropdown
 * selection to eliminate vertical scrolling.
 *
 * @author Aerobook Platform Engineering
 * @version 1.0.0
 */
@DisplayName("Gateway Docs Controller (Swagger UI Multi-Service Dropdown Engine) Tests")
public class GatewayDocsControllerTest {

    private GatewayDocsController controller;

    /**
     * Sets up controller instance before each test.
     */
    @BeforeEach
    public void setUp() {
        controller = new GatewayDocsController();
    }

    /**
     * Verifies that selecting "auth-service" returns only Auth Service endpoints
     * and valid HTTP 200 response with non-null body.
     */
    @Test
    @DisplayName("Verify GET /api/gateway/docs/auth-service returns isolated Auth documentation")
    public void getServiceOpenApi_AuthService_ReturnsIsolatedAuthDocs() {
        // Exercise HTTP controller endpoint
        ResponseEntity<String> response = controller.getServiceOpenApi("auth-service");
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).contains("Authentication");

        // Introspect underlying OpenAPI model
        OpenAPI openAPI = controller.getServiceOpenApiModel("auth-service");
        assertThat(openAPI).isNotNull();
        assertThat(openAPI.getInfo().getTitle()).contains("Authentication");
        assertThat(openAPI.getTags()).hasSize(1);
        assertThat(openAPI.getTags().get(0).getName()).isEqualTo("1. Authentication Service (Port 8082)");

        // Endpoints verification - strictly Auth paths
        assertThat(openAPI.getPaths().containsKey("/api/auth/register")).isTrue();
        assertThat(openAPI.getPaths().containsKey("/api/auth/login")).isTrue();
        assertThat(openAPI.getPaths().containsKey("/api/auth/credentials")).isTrue();
        assertThat(openAPI.getPaths().containsKey("/api/bookings")).isFalse();
        assertThat(openAPI.getPaths().containsKey("/api/v1/users")).isFalse();
        assertThat(openAPI.getPaths().containsKey("/api/flights/search")).isFalse();
    }

    /**
     * Verifies that selecting "user-service" returns only User Service endpoints
     * and valid HTTP 200 response with non-null body.
     */
    @Test
    @DisplayName("Verify GET /api/gateway/docs/user-service returns isolated User documentation")
    public void getServiceOpenApi_UserService_ReturnsIsolatedUserDocs() {
        // Exercise HTTP controller endpoint
        ResponseEntity<String> response = controller.getServiceOpenApi("user-service");
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).contains("User Profile");

        // Introspect underlying OpenAPI model
        OpenAPI openAPI = controller.getServiceOpenApiModel("user-service");
        assertThat(openAPI).isNotNull();
        assertThat(openAPI.getInfo().getTitle()).contains("User Profile");
        assertThat(openAPI.getTags()).hasSize(1);
        assertThat(openAPI.getTags().get(0).getName()).isEqualTo("2. User Profile Service (Port 8084)");

        // Endpoints verification - strictly User paths
        assertThat(openAPI.getPaths().containsKey("/api/v1/users")).isTrue();
        assertThat(openAPI.getPaths().containsKey("/api/v1/users/{id}")).isTrue();
        assertThat(openAPI.getPaths().containsKey("/api/auth/login")).isFalse();
        assertThat(openAPI.getPaths().containsKey("/api/bookings")).isFalse();
    }

    /**
     * Verifies that selecting "flight-service" returns only Flight and Fleet endpoints
     * and valid HTTP 200 response with non-null body.
     */
    @Test
    @DisplayName("Verify GET /api/gateway/docs/flight-service returns isolated Flight documentation")
    public void getServiceOpenApi_FlightService_ReturnsIsolatedFlightDocs() {
        // Exercise HTTP controller endpoint
        ResponseEntity<String> response = controller.getServiceOpenApi("flight-service");
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).contains("Flight");

        // Introspect underlying OpenAPI model
        OpenAPI openAPI = controller.getServiceOpenApiModel("flight-service");
        assertThat(openAPI).isNotNull();
        assertThat(openAPI.getInfo().getTitle()).contains("Flight");
        assertThat(openAPI.getTags()).hasSize(1);
        assertThat(openAPI.getTags().get(0).getName()).isEqualTo("3. Flight Scheduling Service (Port 8087)");

        // Endpoints verification - strictly Flight paths
        assertThat(openAPI.getPaths().containsKey("/api/flights/search")).isTrue();
        assertThat(openAPI.getPaths().containsKey("/api/admin/aircrafts")).isTrue();
        assertThat(openAPI.getPaths().containsKey("/api/admin/flights")).isTrue();
        assertThat(openAPI.getPaths().containsKey("/api/fares")).isFalse();
    }

    /**
     * Verifies that selecting "fare-service" returns only Fare Service endpoints
     * and valid HTTP 200 response with non-null body.
     */
    @Test
    @DisplayName("Verify GET /api/gateway/docs/fare-service returns isolated Fare documentation")
    public void getServiceOpenApi_FareService_ReturnsIsolatedFareDocs() {
        // Exercise HTTP controller endpoint
        ResponseEntity<String> response = controller.getServiceOpenApi("fare-service");
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).contains("Fare");

        // Introspect underlying OpenAPI model
        OpenAPI openAPI = controller.getServiceOpenApiModel("fare-service");
        assertThat(openAPI).isNotNull();
        assertThat(openAPI.getInfo().getTitle()).contains("Fare");
        assertThat(openAPI.getTags()).hasSize(1);
        assertThat(openAPI.getTags().get(0).getName()).isEqualTo("4. Fare & Pricing Service (Port 8089)");

        // Endpoints verification - strictly Fare paths
        assertThat(openAPI.getPaths().containsKey("/api/fares")).isTrue();
        assertThat(openAPI.getPaths().containsKey("/api/fares/{id}")).isTrue();
        assertThat(openAPI.getPaths().containsKey("/api/auth/register")).isFalse();
    }

    /**
     * Verifies that selecting "booking-service" returns only Booking Service endpoints
     * and valid HTTP 200 response with non-null body.
     */
    @Test
    @DisplayName("Verify GET /api/gateway/docs/booking-service returns isolated Booking documentation")
    public void getServiceOpenApi_BookingService_ReturnsIsolatedBookingDocs() {
        // Exercise HTTP controller endpoint
        ResponseEntity<String> response = controller.getServiceOpenApi("booking-service");
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).contains("Booking");

        // Introspect underlying OpenAPI model
        OpenAPI openAPI = controller.getServiceOpenApiModel("booking-service");
        assertThat(openAPI).isNotNull();
        assertThat(openAPI.getInfo().getTitle()).contains("Booking");
        assertThat(openAPI.getTags()).hasSize(1);
        assertThat(openAPI.getTags().get(0).getName()).isEqualTo("5. Booking & Messaging Service (Port 8088)");

        // Endpoints verification - strictly Booking paths
        assertThat(openAPI.getPaths().containsKey("/api/bookings")).isTrue();
        assertThat(openAPI.getPaths().containsKey("/api/bookings/pnr/{pnr}")).isTrue();
        assertThat(openAPI.getPaths().containsKey("/api/check-ins")).isFalse();
    }

    /**
     * Verifies that selecting "check-in-service" returns only Check-In Service endpoints
     * and valid HTTP 200 response with non-null body.
     */
    @Test
    @DisplayName("Verify GET /api/gateway/docs/check-in-service returns isolated Check-In documentation")
    public void getServiceOpenApi_CheckInService_ReturnsIsolatedCheckInDocs() {
        // Exercise HTTP controller endpoint
        ResponseEntity<String> response = controller.getServiceOpenApi("check-in-service");
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).contains("Check-In");

        // Introspect underlying OpenAPI model
        OpenAPI openAPI = controller.getServiceOpenApiModel("check-in-service");
        assertThat(openAPI).isNotNull();
        assertThat(openAPI.getInfo().getTitle()).contains("Check-In");
        assertThat(openAPI.getTags()).hasSize(1);
        assertThat(openAPI.getTags().get(0).getName()).isEqualTo("6. Airport Check-In Service (Port 8090)");

        // Endpoints verification - strictly Check-In paths
        assertThat(openAPI.getPaths().containsKey("/api/check-ins")).isTrue();
        assertThat(openAPI.getPaths().containsKey("/api/check-ins/booking/{bookingId}")).isTrue();
        assertThat(openAPI.getPaths().containsKey("/api/bookings")).isFalse();
    }

    /**
     * Verifies that selecting "gateway" returns only Gateway Hub diagnostic endpoints
     * and valid HTTP 200 response with non-null body.
     */
    @Test
    @DisplayName("Verify GET /api/gateway/docs/gateway returns isolated Gateway Hub documentation")
    public void getServiceOpenApi_Gateway_ReturnsIsolatedGatewayDocs() {
        // Exercise HTTP controller endpoint
        ResponseEntity<String> response = controller.getServiceOpenApi("gateway");
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).contains("API Gateway");

        // Introspect underlying OpenAPI model
        OpenAPI openAPI = controller.getServiceOpenApiModel("gateway");
        assertThat(openAPI).isNotNull();
        assertThat(openAPI.getInfo().getTitle()).contains("API Gateway");
        assertThat(openAPI.getTags()).hasSize(1);
        assertThat(openAPI.getTags().get(0).getName()).isEqualTo("0. API Gateway Platform Hub");

        // Endpoints verification - strictly Gateway paths
        assertThat(openAPI.getPaths().containsKey("/api/gateway/health")).isTrue();
        assertThat(openAPI.getPaths().containsKey("/api/gateway/services")).isTrue();
        assertThat(openAPI.getPaths().containsKey("/api/auth/login")).isFalse();
    }

    /**
     * Verifies that "all" or any unrecognized identifier falls back to the
     * complete platform catalog.
     */
    @Test
    @DisplayName("Verify GET /api/gateway/docs/all returns full platform catalog")
    public void getServiceOpenApi_AllOrUnknown_ReturnsFullPlatformCatalog() {
        // Exercise HTTP controller endpoint
        ResponseEntity<String> response = controller.getServiceOpenApi("all");
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        // Introspect underlying OpenAPI model
        OpenAPI openAPI = controller.getServiceOpenApiModel("all");
        assertThat(openAPI).isNotNull();
        assertThat(openAPI.getTags()).hasSize(7);
        assertThat(openAPI.getPaths().size()).isGreaterThanOrEqualTo(37);
    }

    /**
     * Verifies that unknown or unrecognized service identifiers safely fall back to
     * the complete platform catalog without crashing.
     */
    @Test
    @DisplayName("Verify GET /api/gateway/docs/unknown-service falls back to full platform catalog")
    public void getServiceOpenApi_UnknownService_FallsBackToFullCatalog() {
        ResponseEntity<String> response = controller.getServiceOpenApi("unknown-service");
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        OpenAPI openAPI = controller.getServiceOpenApiModel("unknown-service");
        assertThat(openAPI).isNotNull();
        assertThat(openAPI.getTags()).hasSize(7);
        assertThat(openAPI.getPaths().size()).isGreaterThanOrEqualTo(37);
    }

    /**
     * Verifies that HTTP endpoint GET /api/gateway/docs/{serviceId} returns
     * specification JSON serialized without null $ref fields, preventing Swagger UI
     * "Resolver error at components.securitySchemes.BearerAuth.$ref".
     */
    @Test
    @DisplayName("Verify GET /api/gateway/docs/flight-service returns clean JSON without null $ref")
    public void getServiceOpenApi_ReturnsCleanJsonWithoutNullRef() {
        ResponseEntity<String> response = controller.getServiceOpenApi("flight-service");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).doesNotContain("\"$ref\":null");
        assertThat(response.getBody()).doesNotContain("\"$ref\": null");
        assertThat(response.getBody()).contains("Flight");
        assertThat(response.getBody()).contains("\"type\":\"http\"");
    }
}
