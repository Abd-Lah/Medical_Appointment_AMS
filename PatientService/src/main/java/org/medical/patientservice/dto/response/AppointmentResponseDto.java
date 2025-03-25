package org.medical.patientservice.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AppointmentResponseDto {

    private String id;

    private DoctorResponseDto doctor;

    private LocalDateTime appointmentDate;

    private String billUrl;

    private AppointmentStatusDto status;

    private ReportResponseDto report;
}
