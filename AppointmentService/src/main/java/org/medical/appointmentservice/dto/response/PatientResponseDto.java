package org.medical.appointmentservice.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PatientResponseDto {
    private String id;
    private String firstName;
    private String email;
    private String lastName;
    private String city;
}
