package com.aerobook.configserver;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

import java.io.InputStream;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

class ConfigServerApplicationTests {

    @Test
    void configServerApplicationHasEnableConfigServerAnnotation() {
        assertThat(ConfigServerApplication.class.isAnnotationPresent(SpringBootApplication.class)).isTrue();
        assertThat(ConfigServerApplication.class.isAnnotationPresent(EnableConfigServer.class)).isTrue();
    }

    @Test
    void configServerPropertiesSpecifyPort8080() throws Exception {
        Properties properties = new Properties();
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            assertThat(in).isNotNull();
            properties.load(in);
        }

        assertThat(properties.getProperty("server.port")).isEqualTo("8080");
        assertThat(properties.getProperty("spring.application.name")).isEqualTo("config-server");
    }

    @Test
    void configServerUsesNativeProfileAndSearchLocations() throws Exception {
        Properties properties = new Properties();
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            assertThat(in).isNotNull();
            properties.load(in);
        }

        assertThat(properties.getProperty("spring.profiles.active")).isEqualTo("native");
        assertThat(properties.getProperty("spring.cloud.config.server.native.search-locations")).contains("config-repo");
    }

}
