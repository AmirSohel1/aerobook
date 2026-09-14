package com.aerobook.checkin;

import com.aerobook.checkin.config.SwaggerConfig;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SwaggerConfigTest {

    @Test
    void checkInServiceOpenApiConfigurationIsValid() {
        SwaggerConfig config = new SwaggerConfig();
        OpenAPI openAPI = config.checkInServiceOpenAPI();

        assertThat(openAPI.getInfo().getTitle()).contains("Check-In");
        assertThat(openAPI.getServers()).hasSize(2);
        assertThat(openAPI.getServers().get(0).getUrl()).isEqualTo("http://localhost:8083");
        assertThat(openAPI.getServers().get(1).getUrl()).isEqualTo("http://localhost:8090");

        SecurityScheme scheme = openAPI.getComponents().getSecuritySchemes().get("BearerAuth");
        assertThat(scheme).isNotNull();
        assertThat(scheme.getType()).isEqualTo(SecurityScheme.Type.HTTP);
        assertThat(scheme.getScheme()).isEqualTo("bearer");
    }
}
