package org.medical.patientservice.service;

import org.medical.patientservice.dto.request.AppointmentRequest;
import org.medical.patientservice.dto.request.UserRequest;
import org.medical.patientservice.dto.response.AppointmentResponseDto;
import org.medical.patientservice.dto.response.DoctorResponseDto;
import org.medical.patientservice.dto.response.UserResponseDto;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

public interface PatientService {
    ResponseEntity<Page<DoctorResponseDto>> getDoctors(String firstName, String lastName, String city, String specialization, int page, int size, String sort);
    ResponseEntity<DoctorResponseDto> getDoctor(String doctorId);
    ResponseEntity<UserResponseDto> updateUser(String id, UserRequest userRequest);
    ResponseEntity<String> deleteUser(String id);
    ResponseEntity<String> activateUser(String id);
    
    // Appointment FeignService
    ResponseEntity<Page<AppointmentResponseDto>> getPatientAppointments(String patientId, int page, int size, String orderBy);

    ResponseEntity<AppointmentResponseDto> getPatientAppointment( String patientId,  String appointmentId);

    ResponseEntity<AppointmentResponseDto> createAppointment(AppointmentRequest appointmentRequest);

    ResponseEntity<AppointmentResponseDto> updateAppointment( String appointmentId, AppointmentRequest appointmentRequest);

    ResponseEntity<String> cancelAppointment( String appointmentId,  String patientId);

    ResponseEntity<Resource> appointmentBill( String appointmentId,  String patientId);

}
