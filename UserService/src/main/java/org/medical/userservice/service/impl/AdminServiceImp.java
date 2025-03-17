package org.medical.userservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.medical.userservice.model.RoleEnum;
import org.medical.userservice.model.UserEntity;
import org.medical.userservice.repository.UserRepository;
import org.medical.userservice.service.AdminService;
import org.medical.userservice.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminServiceImp implements AdminService {
    private final UserRepository userRepository;
    private final UserService userService;

    @Override
    public Page<UserEntity> getAllUsersByRole(String role, Pageable pageable) {
        return userRepository.findUsersByRole(RoleEnum.valueOf(role.toUpperCase()),pageable);
    }

    @Override
    @Transactional
    public boolean deleteUser(String id) {
        return userService.deleteAccount(id);
    }

    @Override
    @Transactional
    public boolean validateDoctor(String id) {
        UserEntity doctor = userService.getDoctor(id);
        doctor.setActive(!doctor.getActive());
        userRepository.save(doctor);
        return doctor.getActive();
    }

    @Override
    public Page<UserEntity> search(String firstName, String lastName, String city, String specialization, Pageable pageable) {
        return userService.getAllDoctors(firstName, lastName, city, specialization, pageable);
    }
}
