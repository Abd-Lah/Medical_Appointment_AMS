package org.medical.adminservice.service;

import org.medical.adminservice.dto.UserDtoResponse;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

public interface AdminService {

    ResponseEntity<Page<UserDtoResponse>> getUsers(String role);

    ResponseEntity<Page<UserDtoResponse>> searchDoctors(String firstName, String lastName, String city, String Speciality, int page, int size, String sort);

    ResponseEntity<Page<UserDtoResponse>> searchPatients(
            String firstName,
            String lastName,
            String city,
            int page,
            int size,
            String sort);

    ResponseEntity<UserDtoResponse> getDoctor(String id);

    ResponseEntity<UserDtoResponse> getPatient(String id);

    ResponseEntity<String> deleteUser(String id);

    ResponseEntity<String> validateDoctor(String id);
}
