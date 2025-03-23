package org.medical.appointmentservice.repository;

import org.medical.appointmentservice.model.ReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReportRepository extends JpaRepository<ReportEntity,String> {
    Optional<ReportEntity> findById(String id);

    Optional<ReportEntity> findByAppointmentId(String id);
}