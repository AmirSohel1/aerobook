package com.aerobook.notification.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI notificationServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Aerobook Notification & Alerts API")
                        .description("REST API for customer notifications, airport gate announcements, and flight delay broadcasts.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Aerobook Engineering")
                                .email("dev@aerobook.com"))
                        .license(new License()
                                .name("Apache 2.0")));
    }
}
