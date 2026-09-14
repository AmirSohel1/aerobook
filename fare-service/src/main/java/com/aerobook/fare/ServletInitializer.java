package com.aerobook.fare;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * ============================================================================
 * Fare Service Servlet Initializer
 * ============================================================================
 *
 * Traditional servlet container adapter enabling deployment as an external WAR
 * package inside Apache Tomcat or Eclipse Jetty.
 *
 * @author Aerobook Platform Engineering
 * @version 1.0.0
 */
public class ServletInitializer extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(FareServiceApplication.class);
    }
}
