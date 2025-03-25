package org.medical.patientservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.medical.patientservice.dto.request.AppointmentRequest;
import org.medical.patientservice.dto.request.UserRequest;
import org.medical.patientservice.dto.response.AppointmentResponseDto;
import org.medical.patientservice.dto.response.DoctorResponseDto;
import org.medical.patientservice.dto.response.UserResponseDto;
import org.medical.patientservice.feign.AppointmentFeignClient;
import org.medical.patientservice.feign.UserFeignClient;
import org.medical.patientservice.service.PatientService;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {
    private final UserFeignClient ufc;
    private final AppointmentFeignClient afc;

    @Override
    public ResponseEntity<Page<DoctorResponseDto>> getDoctors(String firstName, String lastName, String city, String specialization, int page, int size, String sort) {
        return ufc.doctor(firstName, lastName, city, specialization, page, size, sort);
    }

    @Override
    public ResponseEntity<DoctorResponseDto> getDoctor(String doctorId) {
        return ufc.doctor(doctorId);
    }

    @Override
    public ResponseEntity<UserResponseDto> updateUser(String id, UserRequest userRequest) {
        return ufc.updateUser(id, userRequest);
    }

    @Override
    public ResponseEntity<String> deleteUser(String id) {
        return ufc.deleteAccount(id);
    }

    @Override
    public ResponseEntity<String> activateUser(String id) {
        return ufc.activateAccount(id);
    }

    @Override
    public ResponseEntity<Page<AppointmentResponseDto>> getPatientAppointments(String patientId, int page, int size, String orderBy) {
        return afc.getPatientAppointments(patientId, page, size, orderBy);
    }

    @Override
    public ResponseEntity<AppointmentResponseDto> getPatientAppointment(String patientId, String appointmentId) {
        return afc.getPatientAppointment(patientId, appointmentId);
    }

    @Override
    public ResponseEntity<AppointmentResponseDto> createAppointment(AppointmentRequest appointmentRequest) {
        return afc.createAppointment(appointmentRequest);
    }

    @Override
    public ResponseEntity<AppointmentResponseDto> updateAppointment(String appointmentId, AppointmentRequest appointmentRequest) {
        return afc.updateAppointment(appointmentId, appointmentRequest);
    }

    @Override
    public ResponseEntity<String> cancelAppointment(String appointmentId, String patientId) {
        return afc.cancelAppointment(appointmentId, patientId);
    }

    @Override
    public ResponseEntity<Resource> appointmentBill(String appointmentId, String patientId) {
        return afc.appointmentBill(appointmentId, patientId);
    }
}
