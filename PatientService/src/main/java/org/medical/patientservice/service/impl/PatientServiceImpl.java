package org.medical.patientservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.medical.patientservice.dto.request.UserRequest;
import org.medical.patientservice.dto.response.DoctorResponseDto;
import org.medical.patientservice.dto.response.UserResponseDto;
import org.medical.patientservice.feign.UserFeignClient;
import org.medical.patientservice.service.PatientService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {
    private final UserFeignClient ufc;

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
}
