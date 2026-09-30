package com.aerobook.user.service;

import java.util.List;

import com.aerobook.user.dto.request.IncidentReportRequest;
import com.aerobook.user.dto.response.IncidentReportResponse;
import com.aerobook.user.entity.IncidentStatus;

public interface IncidentService {

    IncidentReportResponse createIncident(IncidentReportRequest request);

    List<IncidentReportResponse> getAllIncidents(IncidentStatus status);

    IncidentReportResponse getIncidentById(Long id);

    IncidentReportResponse updateIncidentStatus(Long id, IncidentStatus status);

    void deleteIncident(Long id);

    long countOpenIncidents();
}
