package org.medical.appointmentservice.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DoctorResponseDto {

    private String id;

    private String firstName;

    private String lastName;

    private String city;

    private DoctorProfileResponseDto doctorProfile ;

}
