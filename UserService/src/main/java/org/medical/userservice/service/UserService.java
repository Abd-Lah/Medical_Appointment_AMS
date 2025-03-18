package org.medical.userservice.service;

import org.medical.userservice.dto.request.RegisterRequest;
import org.medical.userservice.dto.request.UserRequest;
import org.medical.userservice.model.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    UserEntity createUser(RegisterRequest userRequest);

    UserEntity updateProfile(String userId, UserRequest userRequest);

    boolean deleteAccount(String id);

    Page<UserEntity> getAllDoctors(String firstName, String lastName, String city, String specialization, Pageable pageable);

    UserEntity getDoctor(String id);
}
