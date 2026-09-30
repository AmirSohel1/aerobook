package com.aerobook.user.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aerobook.user.dto.request.IncidentReportRequest;
import com.aerobook.user.dto.response.IncidentReportResponse;
import com.aerobook.user.entity.IncidentReport;
import com.aerobook.user.entity.IncidentSeverity;
import com.aerobook.user.entity.IncidentStatus;
import com.aerobook.user.exception.ResourceNotFoundException;
import com.aerobook.user.repository.IncidentReportRepository;
import com.aerobook.user.service.IncidentService;

import jakarta.annotation.PostConstruct;

@Service
@Transactional
public class IncidentServiceImpl implements IncidentService {

    private static final Logger log = LoggerFactory.getLogger(IncidentServiceImpl.class);

    private final IncidentReportRepository incidentRepository;

    public IncidentServiceImpl(IncidentReportRepository incidentRepository) {
        this.incidentRepository = incidentRepository;
    }

    @PostConstruct
    public void seedInitialIncidents() {
        try {
            if (incidentRepository.count() == 0) {
                log.info("Seeding initial operational incident records into aerobook_user_db");

                IncidentReport inc1 = new IncidentReport(
                        101L,
                        "Vikram Malhotra",
                        "vikram.m@example.com",
                        "Gate Agent EMP-104",
                        "Cabin baggage size exceeded limit at Gate 4B; passenger requested gate check-in",
                        IncidentSeverity.LOW,
                        "Bag checked into hold; tag #AB-99201 issued without dispute"
                );
                inc1.setStatus(IncidentStatus.OPEN);

                IncidentReport inc2 = new IncidentReport(
                        102L,
                        "Pooja Hegde",
                        "pooja.h@example.com",
                        "Desk Staff EMP-202",
                        "Seat duplication report for Flight AB-204 (12B duplicate assignment)",
                        IncidentSeverity.MEDIUM,
                        "Re-assigned to 14A Window; issued ₹1,500 travel voucher"
                );
                inc2.setStatus(IncidentStatus.INVESTIGATING);

                IncidentReport inc3 = new IncidentReport(
                        103L,
                        "Aditya Verma",
                        "aditya.v@example.com",
                        "Supervisor EMP-001",
                        "Severe weather diversion notice communication delay for DEL-BOM sector",
                        IncidentSeverity.HIGH,
                        "Emergency notification SMS broadcast dispatched to all 184 passengers"
                );
                inc3.setStatus(IncidentStatus.RESOLVED);

                incidentRepository.saveAll(List.of(inc1, inc2, inc3));
                log.info("Seeded 3 operational incident reports successfully");
            }
        } catch (Exception e) {
            log.warn("Could not seed initial incidents: {}", e.getMessage());
        }
    }

    @Override
    public IncidentReportResponse createIncident(IncidentReportRequest request) {
        log.info("Recording new incident report for user ID: {} by reporter: {}",
                request.getUserId(), request.getReportedBy());

        IncidentReport report = new IncidentReport(
                request.getUserId(),
                request.getUserName(),
                request.getUserEmail(),
                request.getReportedBy(),
                request.getReason(),
                request.getSeverity() != null ? request.getSeverity() : IncidentSeverity.MEDIUM,
                request.getNotes()
        );
        report.setStatus(IncidentStatus.OPEN);

        IncidentReport saved = incidentRepository.save(report);
        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<IncidentReportResponse> getAllIncidents(IncidentStatus status) {
        List<IncidentReport> reports = (status != null)
                ? incidentRepository.findByStatus(status)
                : incidentRepository.findAll();

        return reports.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public IncidentReportResponse getIncidentById(Long id) {
        IncidentReport report = incidentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Incident report not found with ID: " + id));
        return mapToResponse(report);
    }

    @Override
    public IncidentReportResponse updateIncidentStatus(Long id, IncidentStatus status) {
        IncidentReport report = incidentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Incident report not found with ID: " + id));

        report.setStatus(status);
        IncidentReport updated = incidentRepository.save(report);
        return mapToResponse(updated);
    }

    @Override
    public void deleteIncident(Long id) {
        if (!incidentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Incident report not found with ID: " + id);
        }
        incidentRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public long countOpenIncidents() {
        return incidentRepository.countByStatus(IncidentStatus.OPEN);
    }

    private IncidentReportResponse mapToResponse(IncidentReport entity) {
        return new IncidentReportResponse(
                entity.getId(),
                entity.getUserId(),
                entity.getUserName(),
                entity.getUserEmail(),
                entity.getReportedBy(),
                entity.getReason(),
                entity.getSeverity(),
                entity.getStatus(),
                entity.getNotes(),
                entity.getCreatedAt()
        );
    }
}
