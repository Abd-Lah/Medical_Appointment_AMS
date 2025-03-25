package org.medical.patientservice.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AppointmentRequest {
    private String patientId;
    private String doctorId;
    private LocalDateTime appointmentDate;
}
