package com.aerobook.user.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aerobook.user.dto.request.IncidentReportRequest;
import com.aerobook.user.dto.response.IncidentReportResponse;
import com.aerobook.user.entity.IncidentStatus;
import com.aerobook.user.service.IncidentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Incident Management", description = "Operations for reporting, tracking, and resolving passenger and operational incidents")
@RestController
@RequestMapping("/api/v1/users/incidents")
public class IncidentController {

    private final IncidentService incidentService;

    public IncidentController(IncidentService incidentService) {
        this.incidentService = incidentService;
    }

    @Operation(summary = "List all incident reports", description = "Retrieves all incident reports optionally filtered by status")
    @GetMapping
    public ResponseEntity<List<IncidentReportResponse>> getAllIncidents(
            @RequestParam(required = false) IncidentStatus status) {
        return ResponseEntity.ok(incidentService.getAllIncidents(status));
    }

    @Operation(summary = "Get incident report by ID")
    @GetMapping("/{id}")
    public ResponseEntity<IncidentReportResponse> getIncidentById(@PathVariable Long id) {
        return ResponseEntity.ok(incidentService.getIncidentById(id));
    }

    @Operation(summary = "Create incident report", description = "Reports an unruly passenger, baggage issue, or operational dispute")
    @PostMapping
    public ResponseEntity<IncidentReportResponse> createIncident(@Valid @RequestBody IncidentReportRequest request) {
        IncidentReportResponse response = incidentService.createIncident(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "Update incident status", description = "Transitions status between OPEN, INVESTIGATING, RESOLVED, DISMISSED")
    @PutMapping("/{id}/status")
    public ResponseEntity<IncidentReportResponse> updateStatus(
            @PathVariable Long id,
            @RequestParam IncidentStatus status) {
        return ResponseEntity.ok(incidentService.updateIncidentStatus(id, status));
    }

    @Operation(summary = "Dismiss / Delete incident report")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteIncident(@PathVariable Long id) {
        incidentService.deleteIncident(id);
        return ResponseEntity.ok("Incident report dismissed successfully with ID: " + id);
    }

    @Operation(summary = "Get count of open incidents", description = "Used by Admin dashboard summary probe")
    @GetMapping("/count/open")
    public ResponseEntity<Long> countOpenIncidents() {
        return ResponseEntity.ok(incidentService.countOpenIncidents());
    }
}
