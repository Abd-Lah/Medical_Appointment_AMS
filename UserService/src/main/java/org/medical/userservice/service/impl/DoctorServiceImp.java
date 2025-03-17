package org.medical.userservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.medical.userservice.dto.request.DoctorProfileRequest;
import org.medical.userservice.model.UserEntity;
import org.medical.userservice.repository.UserRepository;
import org.medical.userservice.service.DoctorService;
import org.medical.userservice.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DoctorServiceImp implements DoctorService {
    private final UserService userService;
    private final UserRepository userRepository;


    @Override
    @Transactional
    public UserEntity updateProfile(String id, DoctorProfileRequest profile) {
        UserEntity doctor = userService.getDoctor(id);
        doctor.setDoctorProfile(doctor.getDoctorProfile().updateDoctorProfile(profile));
        return userRepository.save(doctor);
    }
}
