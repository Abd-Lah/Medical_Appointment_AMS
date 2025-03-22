package org.medical.appointmentservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.medical.appointmentservice.dto.request.ReportRequest;

@Entity
@Table(name = "reports")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReportEntity extends BaseEntity {

    @OneToOne
    @JoinColumn(name = "appointment_id", nullable = false, unique = true)
    private AppointmentEntity appointment;

    @Column(columnDefinition = "TEXT")
    private String diagnosis;

    @Column(columnDefinition = "TEXT")
    private String treatment;

    @Column(columnDefinition = "TEXT")
    private String notes;

    public ReportEntity(ReportRequest report, AppointmentEntity appointment) {
        this(appointment, report.getDiagnosis(),report.getTreatment(), report.getNotes());
    }

    public void update(ReportRequest report) {
        diagnosis = report.getDiagnosis();
        treatment = report.getTreatment();
        notes = report.getNotes();
    }
}