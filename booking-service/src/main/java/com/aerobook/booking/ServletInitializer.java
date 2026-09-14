package com.aerobook.booking;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * *
 * ============================================================================
 * * Booking Service Servlet Initializer *
 * ============================================================================
 * * * Configures the Spring application context when deployed as a traditional
 * * WAR archive into an external servlet container (Tomcat, Jetty). * * @author
 * Aerobook Platform Engineering * @version 1.0.0
 */
public class ServletInitializer extends SpringBootServletInitializer {

    /**
     * * Binds {@link BookingServiceApplication} as the configuration bootstrap
     * class. * * @param application the application builder * @return the
     * configured application builder
     */
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(BookingServiceApplication.class);
    }
}
