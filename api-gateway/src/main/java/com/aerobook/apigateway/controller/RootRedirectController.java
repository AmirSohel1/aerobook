package com.aerobook.apigateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

/**
 * ============================================================================
 * Root Context Redirect Controller (Port 8083)
 * ============================================================================
 *
 * Automatically redirects incoming requests at the root context path
 * ({@code /}) to the interactive API Gateway Swagger UI
 * ({@code /swagger-ui.html}).
 *
 * <p>
 * This provides instant developer and evaluator usability when accessing
 * {@code http://localhost:8083/} in a web browser without needing to type the
 * full Swagger path manually.
 *
 * @author Aerobook Platform Engineering
 * @version 1.0.0
 */
@RestController
public class RootRedirectController {

    /**
     * Intercepts GET requests to root path and returns an HTTP 302 Found
     * redirect pointing to {@code /swagger-ui.html}.
     *
     * @return {@link ResponseEntity} configured with 302 status and Location
     * header
     */
    @GetMapping("/")
    public ResponseEntity<Void> redirectToSwagger() {
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create("/swagger-ui.html"))
                .build();
    }
}
