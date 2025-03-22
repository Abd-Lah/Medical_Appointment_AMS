package org.medical.appointmentservice.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PatientResponseDto {
    private int id;
    private String firstName;
    private String lastName;
    private String city;
}
