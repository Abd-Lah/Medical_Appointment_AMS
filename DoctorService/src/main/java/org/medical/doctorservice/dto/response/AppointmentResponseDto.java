package org.medical.doctorservice.dto.response;

import java.time.LocalDateTime;

public class AppointmentResponseDto {
    private String id;

    private PatientResponseDto patient;

    private LocalDateTime appointmentDate;

    private String billUrl;

    private AppointmentStatusDto status;

    private ReportResponseDto report;
}
