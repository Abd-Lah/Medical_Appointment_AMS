package org.medical.adminservice.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.medical.adminservice.dto.UserDtoResponse;
import org.medical.adminservice.feign.UserFeignClient;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final UserFeignClient ufc;
    @Override
    public ResponseEntity<Page<UserDtoResponse>> getUsers(String role) {
        if(role.equalsIgnoreCase("doctor")) {
            return ufc.doctor(null, null, null, null, 0, 10, "firstName,asc");
        }
        return ufc.patient(null, null, null, 0, 10, "firstName,asc");
    }

    @Override
    public ResponseEntity<Page<UserDtoResponse>> searchDoctors(String firstName, String lastName, String city, String Speciality, int page, int size, String sort) {
        return ufc.doctor(firstName, lastName, city, Speciality, page, size, sort);
    }

    @Override
    public ResponseEntity<UserDtoResponse> getDoctor(String id) {
        return ufc.doctor(id);
    }

    @Override
    public ResponseEntity<Page<UserDtoResponse>> searchPatients(String firstName, String lastName, String city, int page, int size, String sort) {
        return ufc.patient(firstName, lastName, city, page, size, sort);
    }

    @Override
    public ResponseEntity<UserDtoResponse> getPatient(String id) {
        return ufc.patient(id);
    }

    @Override
    @Transactional
    public ResponseEntity<String> deleteUser(String id) {
        return ufc.deleteAccount(id);
    }

    @Override
    @Transactional
    public ResponseEntity<String> validateDoctor(String id) {
        return ufc.activateAccount(id);
    }
}
