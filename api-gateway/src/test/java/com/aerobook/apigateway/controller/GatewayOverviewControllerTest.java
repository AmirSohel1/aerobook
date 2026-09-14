package com.aerobook.apigateway.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * ============================================================================
 * Gateway Overview Controller Unit Tests
 * ============================================================================
 *
 * Verifies that {@link GatewayOverviewController} properly returns gateway
 * health status, the 6-microservice architecture directory, the master platform
 * endpoints catalog, the RBAC security policy matrix, and the live presentation
 * script.
 *
 * @author Aerobook Platform Engineering
 */
class GatewayOverviewControllerTest {

    private GatewayOverviewController controller;

    /**
     * Sets up controller instance before each test.
     */
    @BeforeEach
    void setUp() {
        controller = new GatewayOverviewController();
    }

    /**
     * Verifies that getHealth returns HTTP 200 with status UP and service
     * metadata.
     */
    @Test
    @DisplayName("Verify GET /api/gateway/health returns UP status and port 8083")
    void getHealth_ReturnsStatusUpAndGatewayInfo() {
        ResponseEntity<Map<String, Object>> response = controller.getHealth();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("status")).isEqualTo("UP");
        assertThat(response.getBody().get("port")).isEqualTo(8083);
        assertThat(response.getBody().get("service")).isEqualTo("Aerobook API Gateway");
    }

    /**
     * Verifies that getServicesDirectory returns all 6 downstream
     * microservices.
     */
    @Test
    @DisplayName("Verify GET /api/gateway/services returns directory of all 6 microservices")
    void getServicesDirectory_ReturnsAllMicroservices() {
        ResponseEntity<Map<String, Object>> response = controller.getServicesDirectory();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> services = (List<Map<String, Object>>) response.getBody().get("services");
        assertThat(services).hasSize(6);
    }

    /**
     * Verifies that getEndpointsCatalog returns non-empty catalog of platform
     * endpoints.
     */
    @Test
    @DisplayName("Verify GET /api/gateway/endpoints returns non-empty master endpoints catalog")
    void getEndpointsCatalog_ReturnsCatalog() {
        ResponseEntity<Map<String, Object>> response = controller.getEndpointsCatalog();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        @SuppressWarnings("unchecked")
        List<?> endpoints = (List<?>) response.getBody().get("endpoints");
        assertThat(endpoints).isNotEmpty();
    }

    /**
     * Verifies that getRbacMatrix returns supported roles (ROLE_USER,
     * ROLE_ADMIN) and endpoint lists.
     */
    @Test
    @DisplayName("Verify GET /api/gateway/rbac-matrix returns supported roles and policy lists")
    void getRbacMatrix_ReturnsRolesAndPolicyLists() {
        ResponseEntity<Map<String, Object>> response = controller.getRbacMatrix();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        @SuppressWarnings("unchecked")
        List<String> roles = (List<String>) response.getBody().get("rolesSupported");
        assertThat(roles).contains("ROLE_USER", "ROLE_ADMIN");
    }

    /**
     * Verifies that getPresentationGuide returns structured step-by-step
     * evaluator instructions.
     */
    @Test
    @DisplayName("Verify GET /api/gateway/presentation-demo returns structured live demo steps")
    void getPresentationGuide_ReturnsSteps() {
        ResponseEntity<Map<String, Object>> response = controller.getPresentationGuide();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        @SuppressWarnings("unchecked")
        List<String> steps = (List<String>) response.getBody().get("steps");
        assertThat(steps).isNotEmpty();
    }
}
