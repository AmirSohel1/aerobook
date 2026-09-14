package com.aerobook.discovery;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

import java.io.InputStream;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

class ServiceDiscoveryApplicationTests {

    @Test
    void serviceDiscoveryApplicationHasEurekaServerAnnotation() {
        assertThat(ServiceDiscoveryApplication.class.isAnnotationPresent(SpringBootApplication.class)).isTrue();
        assertThat(ServiceDiscoveryApplication.class.isAnnotationPresent(EnableEurekaServer.class)).isTrue();
    }

    @Test
    void serviceDiscoveryPropertiesSpecifyPort8761() throws Exception {
        Properties properties = new Properties();
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            assertThat(in).isNotNull();
            properties.load(in);
        }

        assertThat(properties.getProperty("server.port")).isEqualTo("8761");
        assertThat(properties.getProperty("spring.application.name")).isEqualTo("SERVICE-DISCOVERY");
    }

    @Test
    void serviceDiscoveryDisablesSelfRegistration() throws Exception {
        Properties properties = new Properties();
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            assertThat(in).isNotNull();
            properties.load(in);
        }

        assertThat(properties.getProperty("eureka.client.register-with-eureka")).isEqualTo("false");
        assertThat(properties.getProperty("eureka.client.fetch-registry")).isEqualTo("false");
    }

}
