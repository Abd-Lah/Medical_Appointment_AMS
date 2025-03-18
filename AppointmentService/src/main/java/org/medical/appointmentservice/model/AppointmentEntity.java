package org.medical.appointmentservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "appointments")
@Getter
@Setter
@NoArgsConstructor
public class AppointmentEntity extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private String patient;

    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private UserEntity doctor;

    @Column(nullable = false, name = "appointment_date")
    private LocalDateTime appointmentDate;

    @Column(nullable = false)
    private AppointmentStatus Status = AppointmentStatus.PENDING;

    @OneToOne(mappedBy = "appointment", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private ReportEntity report;


    public AppointmentEntity(UserEntity loggedPatient, UserEntity doctor, LocalDateTime appointmentDate, AppointmentStatus appointmentStatus, ReportEntity report) {
        this.patient = loggedPatient;
        this.doctor = doctor;
        this.appointmentDate = appointmentDate;
        this.Status = appointmentStatus;
        this.report = report;
    }
}