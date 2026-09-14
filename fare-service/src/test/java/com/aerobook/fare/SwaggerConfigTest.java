package com.aerobook.fare;

import com.aerobook.fare.config.SwaggerConfig;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SwaggerConfigTest {

    @Test
    void fareServiceOpenApiConfigurationIsValid() {
        SwaggerConfig config = new SwaggerConfig();
        OpenAPI openAPI = config.fareServiceOpenAPI();

        assertThat(openAPI.getInfo().getTitle()).contains("Fare");
        assertThat(openAPI.getServers()).hasSize(2);
        assertThat(openAPI.getServers().get(0).getUrl()).isEqualTo("http://localhost:8083");
        assertThat(openAPI.getServers().get(1).getUrl()).isEqualTo("http://localhost:8089");

        SecurityScheme scheme = openAPI.getComponents().getSecuritySchemes().get("BearerAuth");
        assertThat(scheme).isNotNull();
        assertThat(scheme.getType()).isEqualTo(SecurityScheme.Type.HTTP);
        assertThat(scheme.getScheme()).isEqualTo("bearer");
    }
}
