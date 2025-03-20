package org.medical.doctorservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.medical.doctorservice.model.DoctorProfileEntity;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DoctorProfileRequest {

    private String doctorId;

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

    public DoctorProfileEntity createProfile() {
        return new DoctorProfileEntity(this);
    }
}