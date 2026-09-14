package com.aerobook.checkin;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * *
 * ============================================================================
 * * Check-In Service Servlet Initializer *
 * ============================================================================
 * * * Configures the Spring application context when deployed as a traditional
 * * WAR archive in standalone servlet containers (Tomcat, Jetty). * * @author
 * Aerobook Platform Engineering * @version 1.0.0
 */
public class ServletInitializer extends SpringBootServletInitializer {

    /**
     * * Binds {@link CheckInServiceApplication} as the primary configuration
     * source. * * @param application the Spring application builder * @return
     * the configured builder instance
     */
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(CheckInServiceApplication.class);
    }
}
