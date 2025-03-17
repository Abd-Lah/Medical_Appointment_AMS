package org.medical.userservice.service;

import org.medical.userservice.model.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminService {
    Page<UserEntity> getAllUsersByRole(String role, Pageable pageable);
    boolean deleteUser(String id);
    boolean validateDoctor(String id);

    Page<UserEntity> search(String firstName, String lastName, String city, String specialization, Pageable pageable);
}
