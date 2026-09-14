package com.aerobook.flight;

import com.aerobook.flight.config.SwaggerConfig;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests ensuring Swagger / OpenAPI documentation is generated correctly
 * without missing configuration.
 */
class SwaggerConfigTest {

    /**
     * Asserts that the Flight Service OpenAPI bean generates title, servers,
     * and BearerAuth properly.
     */
    @Test
    void flightServiceOpenApiConfigurationIsValid() {
        SwaggerConfig config = new SwaggerConfig();
        OpenAPI openAPI = config.customOpenAPI();

        assertThat(openAPI.getInfo().getTitle()).contains("Flight");
        assertThat(openAPI.getServers()).hasSize(2);
        assertThat(openAPI.getServers().get(0).getUrl()).isEqualTo("http://localhost:8083");
        assertThat(openAPI.getServers().get(1).getUrl()).isEqualTo("http://localhost:8087");

        SecurityScheme scheme = openAPI.getComponents().getSecuritySchemes().get("BearerAuth");
        assertThat(scheme).isNotNull();
        assertThat(scheme.getType()).isEqualTo(SecurityScheme.Type.HTTP);
        assertThat(scheme.getScheme()).isEqualTo("bearer");
    }
}
