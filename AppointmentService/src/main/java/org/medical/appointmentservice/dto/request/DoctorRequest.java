package org.medical.appointmentservice.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class DoctorRequest {

    private String id;

    private DoctorProfileRequest doctorProfile;

}
