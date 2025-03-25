package org.medical.appointmentservice.dto.response;

import lombok.Getter;
import lombok.Setter;
import org.medical.appointmentservice.model.AppointmentStatus;

import java.time.LocalDateTime;

@Getter
@Setter
public class AppointmentPatientResponseDto {

    private String id;

    private DoctorResponseDto doctor;

    private LocalDateTime appointmentDate;

    private String billUrl;

    private AppointmentStatus status;

    private ReportResponseDto report;

}
