package org.medical.doctorservice.service;

import org.medical.doctorservice.dto.request.AppointmentStatusRequest;
import org.medical.doctorservice.dto.request.DoctorProfileRequest;
import org.medical.doctorservice.dto.request.ReportRequest;
import org.medical.doctorservice.dto.response.AppointmentResponseDto;
import org.medical.doctorservice.dto.response.ReportResponseDto;
import org.medical.doctorservice.model.DoctorProfileEntity;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

public interface DoctorService {
    DoctorProfileEntity getProfile(String id);
    DoctorProfileEntity createProfile(DoctorProfileRequest request);
    DoctorProfileEntity updateProfile(String id, DoctorProfileRequest profile);
    void deleteProfile(String id);
    void activateProfile(String id);
    
    //Appointment Feign

    ResponseEntity<Page<AppointmentResponseDto>> getDoctorAppointments( String doctorId, int page, int size, String orderBy);
    ResponseEntity<AppointmentResponseDto> getDoctorAppointment( String doctorId, String appointmentId);
    ResponseEntity<AppointmentResponseDto> changeStatus(String appointmentId,  String doctorId, AppointmentStatusRequest appointmentStatusRequest);
    ResponseEntity<ReportResponseDto> createReport(String appointmentId, ReportRequest reportCommand);
    ResponseEntity<ReportResponseDto> updateReport(String reportId, ReportRequest reportCommand);
}
