package org.medical.appointmentservice.dto.response;

import lombok.Getter;
import lombok.Setter;
import org.medical.appointmentservice.model.AppointmentStatus;

import java.time.LocalDateTime;
@Getter
@Setter

public class AppointmentDoctorResponseDto {

    private String id;

    private PatientResponseDto patient;

    private LocalDateTime appointmentDate;

    private String billUrl;

    private AppointmentStatus status;

    private ReportResponseDto report;

}
