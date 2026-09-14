package com.aerobook.user;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * ============================================================================
 * Servlet Initializer for Traditional WAR Deployment
 * ============================================================================
 *
 * Configures the application when deployed as a WAR archive into an external
 * servlet container (such as Apache Tomcat or Jetty). Binds the
 * {@link UserServiceApplication} bootstrap class to the Spring lifecycle.
 *
 * @author Aerobook Platform Engineering
 * @version 1.0.0
 */
public class ServletInitializer extends SpringBootServletInitializer {

    /**
     * Configures the application by binding {@link UserServiceApplication} as
     * the primary configuration source.
     *
     * @param application the application builder
     * @return the configured application builder
     */
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(UserServiceApplication.class);
    }

}
