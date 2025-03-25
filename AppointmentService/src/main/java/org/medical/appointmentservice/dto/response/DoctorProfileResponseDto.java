package org.medical.appointmentservice.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DoctorProfileResponseDto {

    private String id;

    private String clinicAddress;

    private String specialty ;

    private Integer appointmentDuration ;

    private String workingDays ;

    private String startTime ;

    private String breakTimeStart ;

    private String breakTimeEnd ;

    private String endTime ;

}
