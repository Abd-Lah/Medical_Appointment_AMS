package org.medical.appointmentservice.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DoctorProfileRequest {

    private Integer appointmentDuration ;

    private String workingDays ;

    private String startTime ;

    private String breakTimeStart ;

    private String breakTimeEnd ;

    private String endTime ;

}
