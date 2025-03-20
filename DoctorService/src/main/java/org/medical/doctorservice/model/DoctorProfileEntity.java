package org.medical.doctorservice.model;


import jakarta.persistence.*;
import lombok.*;
import org.medical.doctorservice.dto.request.DoctorProfileRequest;

@Entity
@Table(name = "doctor_profiles")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DoctorProfileEntity extends BaseEntity{
    @Column(name = "doctor_id" ,unique = true)
    private String doctorId ;

    @Column(columnDefinition = "TEXT")
    private String bio;

    private String experience;

    @Column(columnDefinition = "TEXT")
    private String qualifications;

    private String clinicAddress;

    @Column(nullable = false)
    private String specialty = "General";

    @Column(nullable = false)
    private Integer appointmentDuration = 30; // Default 30 minutes

    @Column(nullable = false)
    private String workingDays = "Monday, Tuesday, Wednesday, Thursday, Friday, Saturday";

    @Column(nullable = false, name = "start_time")
    private String startTime = "09:00";

    @Column(nullable = false, name = "break_time_start")
    private String breakTimeStart = "13:00";

    @Column(nullable = false, name = "break_time_end")
    private String breakTimeEnd = "14:00";

    @Column(nullable = false, name = "end_time")
    private String endTime = "22:00";

    public DoctorProfileEntity(DoctorProfileRequest request) {
        this.doctorId = request.getDoctorId();
        this.bio = request.getBio();
        this.experience = request.getExperience();
        this.qualifications = request.getQualifications();
        this.clinicAddress = request.getClinicAddress();
        this.specialty = request.getSpecialty();
        this.appointmentDuration = request.getAppointmentDuration();
        this.workingDays = request.getWorkingDays();
        this.startTime = request.getStartTime();
        this.breakTimeStart = request.getBreakTimeStart();
        this.breakTimeEnd = request.getBreakTimeEnd();
        this.endTime = request.getEndTime();
    }

    public DoctorProfileEntity updateDoctorProfile(DoctorProfileRequest p) {
        this.setBio(p.getBio());
        this.setExperience(p.getExperience());
        this.setQualifications(p.getQualifications());
        this.setClinicAddress(p.getClinicAddress());
        this.setSpecialty(p.getSpecialty());
        this.setAppointmentDuration(p.getAppointmentDuration());
        this.setWorkingDays(p.getWorkingDays());
        this.setStartTime(p.getStartTime());
        this.setBreakTimeStart(p.getBreakTimeStart());
        this.setBreakTimeEnd(p.getBreakTimeEnd());
        this.setEndTime(p.getEndTime());
        return this;
    }
}

