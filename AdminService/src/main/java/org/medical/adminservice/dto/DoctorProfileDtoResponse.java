package org.medical.adminservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DoctorProfileDtoResponse {

    private String id;

    private String bio;

    private String experience;

    private String qualifications;

    private String clinicAddress;

    private String specialty ;

    private Integer appointmentDuration ;

    private String workingDays ;

    private String startTime ;

    private String breakTimeStart ;

    private String breakTimeEnd ;

    private String endTime ;
}
