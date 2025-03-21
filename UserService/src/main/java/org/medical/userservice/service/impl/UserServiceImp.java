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
        return filterUsers(firstName, lastName, city, specialization, RoleEnum.DOCTOR, pageable);
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
    public Page<UserEntity> getAllPatients(String firstName, String lastName, String city, Pageable pageable) {
        return getUsers(firstName, lastName, city, RoleEnum.PATIENT, pageable);
    }

    @Override
    public UserEntity getPatient(String id) {
        UserEntity patient = userRepository.getPatient(id);
        helper.isObjectNull(patient, "Patient not found");
        return patient;
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

    private Page<UserEntity> getUsers(String firstName, String lastName, String city,RoleEnum role, Pageable pageable) {
        return userRepository.getUsersBySpec(firstName, lastName, city,role, pageable);
    }

    private Page<UserEntity> filterUsers(String firstName, String lastName, String city, String specialization, RoleEnum role, Pageable pageable) {
        // Get the initial page of users based on the provided filters
        Page<UserEntity> users = getUsers(firstName, lastName, city, role, pageable);

        // Only filter if the role is DOCTOR and specialization is provided
        if (role == RoleEnum.DOCTOR) {
            List<UserEntity> filteredDoctors = new ArrayList<>();

            // Loop through the users and filter based on the specialization (if it's not null)
            for (UserEntity doctor : users.getContent()) {
                DoctorProfileDtoResponse doctorProfile = doctorServiceClient.getDoctorProfile(doctor.getId());

                if (doctorProfile != null) {
                    // If specialization is provided, filter based on it
                    if (specialization != null && !specialization.isEmpty()) {
                        if (doctorProfile.getSpecialty() != null && doctorProfile.getSpecialty().toLowerCase().contains(specialization.toLowerCase())) {
                            doctor.setDoctorProfile(doctorProfile);
                            filteredDoctors.add(doctor);
                        }
                    } else {
                        // If specialization is null, add doctor to the list regardless of specialization
                        doctor.setDoctorProfile(doctorProfile);
                        filteredDoctors.add(doctor);
                    }
                }
            }

            // Return the filtered list as a Page
            return new PageImpl<>(filteredDoctors, pageable, filteredDoctors.size());
        }

        // Return all users if the role is not DOCTOR or if no filtering is needed
        return users;
    }

}
