package org.medical.patientservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DoctorResponseDto {

    private String id;

    private String firstName;

    private String lastName;

    private String city;

    private DoctorProfileResponseDto doctorProfile ;
}
