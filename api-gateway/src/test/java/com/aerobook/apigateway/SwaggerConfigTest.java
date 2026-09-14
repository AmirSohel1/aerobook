package com.aerobook.apigateway;

import com.aerobook.apigateway.config.GatewayOpenApiPathsBuilder;
import com.aerobook.apigateway.config.SwaggerConfig;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * ============================================================================
 * OpenAPI & Swagger Configuration Unit Tests
 * ============================================================================
 *
 * Verifies that the programmatic OpenAPI builder produces valid, complete
 * OpenAPI 3 specifications containing all 7 microservice categories, all
 * platform paths, all 17 DTO component schemas, and valid BearerAuth security
 * configuration.
 *
 * @author Aerobook Platform Engineering
 */
class SwaggerConfigTest {

    private OpenAPI openAPI;

    /**
     * Initializes a fresh OpenAPI configuration before each test execution.
     */
    @BeforeEach
    void setUp() {
        SwaggerConfig config = new SwaggerConfig();
        openAPI = config.gatewayOpenAPI();
    }

    /**
     * Verifies that the global OpenAPI specification contains valid title,
     * reverse-proxy server URL (http://localhost:8083), and BearerAuth JWT
     * security scheme.
     */
    @Test
    @DisplayName("Verify OpenAPI title, reverse proxy server URL, and JWT security scheme")
    void gatewayOpenApiConfigurationIsValid() {
        assertThat(openAPI).isNotNull();
        assertThat(openAPI.getInfo().getTitle()).contains("Aerobook");
        assertThat(openAPI.getServers()).isNotEmpty();
        assertThat(openAPI.getServers().get(0).getUrl()).isEqualTo("http://localhost:8083");

        assertThat(openAPI.getComponents()).isNotNull();
        SecurityScheme securityScheme = openAPI.getComponents().getSecuritySchemes().get("BearerAuth");
        assertThat(securityScheme).isNotNull();
        assertThat(securityScheme.getType()).isEqualTo(SecurityScheme.Type.HTTP);
        assertThat(securityScheme.getScheme()).isEqualTo("bearer");
        assertThat(securityScheme.getBearerFormat()).isEqualTo("JWT");

        assertThat(openAPI.getSecurity()).isNotEmpty();
        assertThat(openAPI.getSecurity().get(0).containsKey("BearerAuth")).isTrue();
    }

    /**
     * Verifies that serializing OpenAPI to JSON via Swagger's Json mapper does
     * not produce null $ref fields on security schemes or components,
     * preventing Swagger UI "Resolver error at
     * components.securitySchemes.BearerAuth.$ref".
     */
    @Test
    @DisplayName("Verify OpenAPI JSON serialization does not contain null $ref on security schemes")
    void openApiSerializationDoesNotContainNullRefOnSecuritySchemes() throws Exception {
        String json = io.swagger.v3.core.util.Json.mapper().writeValueAsString(openAPI);
        assertThat(json).doesNotContain("\"$ref\":null");
        assertThat(json).doesNotContain("\"$ref\": null");
        assertThat(json).contains("\"type\":\"http\"");
        assertThat(json).contains("\"scheme\":\"bearer\"");
    }

    /**
     * Verifies that all 7 microservice tags are registered in the OpenAPI
     * document.
     */
    @Test
    @DisplayName("Verify all 7 microservice tags are registered")
    void gatewayOpenApiContainsAllPlatformTagsAndServices() {
        assertThat(openAPI.getTags()).hasSize(7);
        assertThat(openAPI.getTags()).extracting("name").contains(
                "0. API Gateway Platform Hub",
                "1. Authentication Service (Port 8082)",
                "2. User Profile Service (Port 8084)",
                "3. Flight Scheduling Service (Port 8087)",
                "4. Fare & Pricing Service (Port 8089)",
                "5. Booking & Messaging Service (Port 8088)",
                "6. Airport Check-In Service (Port 8090)"
        );
    }

    /**
     * Verifies that all platform endpoints across all microservices are mapped
     * in the OpenAPI paths.
     */
    @Test
    @DisplayName("Verify OpenAPI paths contain representative endpoints from all microservices")
    void gatewayOpenApiContainsAllPlatformPaths() {
        assertThat(openAPI.getPaths()).isNotNull();
        assertThat(openAPI.getPaths().size()).isGreaterThanOrEqualTo(37);

        // Verify representative paths from all 7 services
        assertThat(openAPI.getPaths().containsKey("/api/gateway/health")).isTrue();
        assertThat(openAPI.getPaths().containsKey("/api/auth/register")).isTrue();
        assertThat(openAPI.getPaths().containsKey("/api/auth/login")).isTrue();
        assertThat(openAPI.getPaths().containsKey("/api/v1/users")).isTrue();
        assertThat(openAPI.getPaths().containsKey("/api/flights/search")).isTrue();
        assertThat(openAPI.getPaths().containsKey("/api/admin/flights")).isTrue();
        assertThat(openAPI.getPaths().containsKey("/api/fares")).isTrue();
        assertThat(openAPI.getPaths().containsKey("/api/bookings")).isTrue();
        assertThat(openAPI.getPaths().containsKey("/api/check-ins")).isTrue();
    }

    /**
     * Verifies that all 17 DTO component schemas are registered with
     * definitions.
     */
    @Test
    @DisplayName("Verify all 17 DTO models are present in OpenAPI components")
    void gatewayOpenApiContainsAllComponentsAndSchemas() {
        assertThat(openAPI.getComponents().getSchemas()).isNotNull();
        var schemas = openAPI.getComponents().getSchemas();

        assertThat(schemas).containsKeys(
                "RegisterRequest",
                "LoginRequest",
                "RefreshTokenRequest",
                "AuthResponse",
                "CredentialResponse",
                "UserResponse",
                "CreateFlightRequest",
                "FlightResponse",
                "Aircraft",
                "FareRequest",
                "FareResponse",
                "PassengerRequest",
                "BookingRequest",
                "BookingResponse",
                "CheckInRequest",
                "CheckIn",
                "ErrorResponse"
        );
    }

    /**
     * Verifies that per-service OpenAPI builders produce isolated
     * specifications containing strictly one service tag and configuring the
     * gateway reverse proxy server.
     */
    @Test
    @DisplayName("Verify per-service OpenAPI builders produce isolated specs with proxy server")
    void perServiceOpenApiBuildersProduceIsolatedSpecsWithProxyServer() {
        String[] services = {"auth-service", "user-service", "flight-service", "fare-service", "booking-service", "check-in-service", "gateway"};

        for (String service : services) {
            OpenAPI serviceDoc = GatewayOpenApiPathsBuilder.buildServiceOpenAPI(service);
            assertThat(serviceDoc).isNotNull();
            assertThat(serviceDoc.getServers()).hasSize(1);
            assertThat(serviceDoc.getServers().get(0).getUrl()).isEqualTo("http://localhost:8083");
            assertThat(serviceDoc.getTags()).hasSize(1);
            assertThat(serviceDoc.getPaths()).isNotEmpty();
            assertThat(serviceDoc.getComponents().getSchemas()).isNotEmpty();
        }
    }
}
