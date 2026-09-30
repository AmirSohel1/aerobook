package com.aerobook.user.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aerobook.user.entity.IncidentReport;
import com.aerobook.user.entity.IncidentStatus;

@Repository
public interface IncidentReportRepository extends JpaRepository<IncidentReport, Long> {

    List<IncidentReport> findByStatus(IncidentStatus status);

    List<IncidentReport> findByUserId(Long userId);

    long countByStatus(IncidentStatus status);
}
