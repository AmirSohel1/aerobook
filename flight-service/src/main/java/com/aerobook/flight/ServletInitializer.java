package com.aerobook.flight;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * Servlet initializer supporting WAR deployment to external servlet containers
 * (e.g. Apache Tomcat).
 */
public class ServletInitializer extends SpringBootServletInitializer {

    /**
     * Configures the application when deployed as a WAR archive.
     *
     * @param application the Spring application builder
     * @return the configured application builder
     */
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(FlightServiceApplication.class);
    }

}
