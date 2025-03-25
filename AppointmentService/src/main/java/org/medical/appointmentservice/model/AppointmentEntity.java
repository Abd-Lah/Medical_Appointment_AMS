package org.medical.appointmentservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "appointments")
@Getter
@Setter
@NoArgsConstructor
public class AppointmentEntity extends BaseEntity {

    @Column(name = "patient_id", nullable = false)
    private String patientId;

    @Column(name = "doctor_id", nullable = false)
    private String doctorId;

    @Column(nullable = false, name = "appointment_date")
    private LocalDateTime appointmentDate;

    @Column(nullable = false)
    private AppointmentStatus Status = AppointmentStatus.PENDING;

    @OneToOne(mappedBy = "appointment", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private ReportEntity report;


    public AppointmentEntity(String patientId, String doctorId, LocalDateTime appointmentDate, AppointmentStatus appointmentStatus, ReportEntity report) {
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.appointmentDate = appointmentDate;
        this.Status = appointmentStatus;
        this.report = report;
    }
}