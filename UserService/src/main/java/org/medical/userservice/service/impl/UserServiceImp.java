package org.medical.userservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.medical.userservice.dto.request.DoctorProfileRequest;
import org.medical.userservice.dto.request.RegisterRequest;
import org.medical.userservice.dto.request.UserRequest;
import org.medical.userservice.dto.response.DoctorProfileDtoResponse;
import org.medical.userservice.feign.DoctorServiceClient;
import org.medical.userservice.model.RoleEnum;
import org.medical.userservice.model.UserEntity;
import org.medical.userservice.repository.UserRepository;
import org.medical.userservice.service.UserService;
import org.medical.userservice.util.Helper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImp implements UserService {
    private final UserRepository userRepository;
    private final DoctorServiceClient doctorServiceClient;
    private final Helper<UserEntity> helper;
    private final PasswordEncoder passwordEncoder;


    @Override
    public Page<UserEntity> getAllDoctors(String firstName, String lastName, String city, String specialization, Pageable pageable) {
        return getDoctors(firstName, lastName, city, specialization, pageable);
    }

    @Override
    @Transactional
    public UserEntity createUser(RegisterRequest userRequest) {
        userRequest.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        UserEntity newUser = userRepository.save(userRequest.toUserEntity());
        DoctorProfileDtoResponse profile = createDoctorProfile(userRequest, newUser);
        newUser.setDoctorProfile(profile);
        return newUser;
    }

    @Override
    @Transactional
    public UserEntity update(String id, UserRequest userRequest) {
        UserEntity oldUser = getUserById(id);
        helper.isObjectNull(oldUser, "User not found");
        assert oldUser != null;
        oldUser.updateUserProfile(userRequest);
        return userRepository.save(oldUser);
    }

    @Override
    @Transactional
    public void deleteAccount(String id) {
        UserEntity user = getUserById(id);
        helper.isObjectNull(user, "User not found");
        if(user.getDoctorProfile() != null) {
            doctorServiceClient.deleteDoctorProfile(user.getDoctorProfile().getId());
        }
        user.setDeleted(true);
        userRepository.save(user);
    }



    @Override
    public UserEntity getDoctor(String id) {
        UserEntity doctor = getUserById(id);
        helper.isObjectNull(doctor, "Doctor not found");
        doctor.setDoctorProfile(doctorServiceClient.getDoctorProfile(doctor.getId()));
        return doctor;
    }

    @Override
    public void activateAccount(String id) {
        UserEntity user = getUserById(id);
        helper.isObjectNull(user, "User not found");
        if(user.getDoctorProfile() != null) {
            doctorServiceClient.activateDoctorProfile(user.getDoctorProfile().getId());
        }
        user.setDeleted(false);
        userRepository.save(user);
    }


    private UserEntity getUserById(String id) {

        return userRepository.findById(id).orElse(null);
    }

    private DoctorProfileDtoResponse createDoctorProfile(RegisterRequest user, UserEntity userEntity) {
        if(user.getRole() == RoleEnum.DOCTOR){
            DoctorProfileRequest profileRequest = user.getDoctorProfile();
            profileRequest.setDoctorId(userEntity.getId());
            return doctorServiceClient.createDoctorProfile(profileRequest);
        }
        return null;
    }

    private Page<UserEntity> getDoctors(String firstName, String lastName, String city, String specialization, Pageable pageable) {
        // Step 1: Fetch doctors using the specification from UserRepository
        Page<UserEntity> doctors = userRepository.getDoctorsBySpec(firstName, lastName, city, pageable);

        // Step 2: Fetch specialization for each doctor and filter by specialization
        List<UserEntity> filteredDoctors = new ArrayList<>();

        for (UserEntity doctor : doctors.getContent()) {
            // Fetch the doctor profile to get specialization
            DoctorProfileDtoResponse doctorProfile = doctorServiceClient.getDoctorProfile(doctor.getId());

            // If specialization is not specified, add all doctors
            if (specialization == null || specialization.isEmpty()) {
                doctor.setDoctorProfile(doctorProfile);
                filteredDoctors.add(doctor);
            } else {
                // Filter by specialization if it matches
                if (doctorProfile != null && doctorProfile.getSpecialty() != null &&
                        doctorProfile.getSpecialty().toLowerCase().contains(specialization.toLowerCase())) {
                    doctor.setDoctorProfile(doctorProfile);
                    filteredDoctors.add(doctor);
                }
            }
        }

        // Step 3: Return a new page with the filtered list of doctors
        return new PageImpl<>(filteredDoctors, pageable, filteredDoctors.size());
    }
}
