package org.medical.userservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.medical.userservice.dto.request.RegisterRequest;
import org.medical.userservice.dto.request.UserRequest;
import org.medical.userservice.model.UserEntity;
import org.medical.userservice.repository.UserRepository;
import org.medical.userservice.service.UserService;
import org.medical.userservice.util.Helper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImp implements UserService {
    private final UserRepository userRepository;
    private final Helper<UserEntity> helper;

    @Override
    @Transactional
    public UserEntity createUser(RegisterRequest userRequest) {
        UserEntity newUser = userRequest.toUserEntity();
        return userRepository.save(newUser);
    }

    @Override
    @Transactional
    public UserEntity updateProfile(String id, UserRequest userRequest) {
        UserEntity oldUser = getUserById(id);
        helper.isObjectNull(oldUser, "User not found");
        assert oldUser != null;
        oldUser.updateUserProfile(userRequest);
        return userRepository.save(oldUser);
    }

    @Override
    @Transactional
    public boolean deleteAccount(String id) {
        UserEntity user = getUserById(id);
        user.setDeleted(!user.getDeleted());
        userRepository.save(user);
        return user.getDeleted();
    }

    @Override
    public Page<UserEntity> getAllDoctors(String firstName, String lastName, String city, String specialization, Pageable pageable) {
        return userRepository.getDoctors(firstName, lastName, city, specialization, pageable);
    }

    @Override
    public UserEntity getDoctor(String id) {
        UserEntity doctor = getUserById(id);
        helper.isObjectNull(doctor, "Doctor not found");
        return doctor;
    }


    private UserEntity getUserById(String id) {
        return userRepository.findById(id).orElse(null);
    }
}
