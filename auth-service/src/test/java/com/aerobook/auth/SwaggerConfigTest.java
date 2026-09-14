package com.aerobook.auth;

import com.aerobook.auth.config.SwaggerConfig;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit test suite verifying OpenAPI 3 documentation specification bean
 * configuration.
 *
 * @author AeroBook Development Team
 * @version 1.0
 */
@DisplayName("Swagger / OpenAPI Configuration Tests")
class SwaggerConfigTest {

    /**
     * Verifies that OpenAPI bean initializes title, multiple server URLs
     * (Gateway and Direct), and the BearerAuth HTTP security scheme.
     */
    @Test
    @DisplayName("Should verify OpenAPI configuration metadata and security schemes")
    void authServiceOpenApiConfigurationIsValid() {
        SwaggerConfig config = new SwaggerConfig();
        OpenAPI openAPI = config.authServiceOpenAPI();

        assertThat(openAPI.getInfo().getTitle()).contains("Auth Service");
        assertThat(openAPI.getServers()).hasSize(2);
        assertThat(openAPI.getServers().get(0).getUrl()).isEqualTo("http://localhost:8083");
        assertThat(openAPI.getServers().get(1).getUrl()).isEqualTo("http://localhost:8082");

        SecurityScheme scheme = openAPI.getComponents().getSecuritySchemes().get("BearerAuth");
        assertThat(scheme).isNotNull();
        assertThat(scheme.getType()).isEqualTo(SecurityScheme.Type.HTTP);
        assertThat(scheme.getScheme()).isEqualTo("bearer");
    }
}
