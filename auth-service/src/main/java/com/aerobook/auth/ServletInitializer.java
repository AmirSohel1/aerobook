package com.aerobook.auth;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * ============================================================================
 * Traditional Web Application Archive (WAR) Deployment Initializer
 * ============================================================================
 *
 * Configures the application when deployed as a WAR package inside an external
 * servlet container (e.g. Apache Tomcat).
 *
 * @author Aerobook Security Engineering
 */
public class ServletInitializer extends SpringBootServletInitializer {

    /**
     * Binds the Spring application builder to the primary
     * {@link AuthServiceApplication} configuration.
     *
     * @param application builder for the Spring application context
     * @return configured {@link SpringApplicationBuilder}
     */
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(AuthServiceApplication.class);
    }
}
