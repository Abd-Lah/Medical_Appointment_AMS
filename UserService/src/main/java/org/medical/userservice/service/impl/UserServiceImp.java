package org.medical.userservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.medical.userservice.dto.request.DoctorProfileRequest;
import org.medical.userservice.dto.request.RegisterRequest;
import org.medical.userservice.dto.request.UserRequest;
import org.medical.userservice.service.SimpleJwtService;
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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.medical.userservice.feign.GatewayClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserServiceImp implements UserService {
    private final UserRepository userRepository;
    private final DoctorServiceClient doctorServiceClient;
    private final Helper<UserEntity> helper;
    private final PasswordEncoder passwordEncoder;
    private final SimpleJwtService jwtService;
    private final JwtDecoder jwtDecoder;
    private final GatewayClient gatewayClient;

    // In-memory token blacklists (replace with Redis in production)
    private final List<String> blacklistedAccessTokens = new ArrayList<>();
    private final List<String> blacklistedRefreshTokens = new ArrayList<>();


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
    public UserEntity getUser(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));
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

    @Override
    public String refreshAccessToken(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new IllegalArgumentException("Refresh token is required");
        }
        if (blacklistedRefreshTokens.contains(refreshToken)) {
            throw new IllegalStateException("Refresh token is revoked");
        }
        try {
            Jwt jwt = jwtDecoder.decode(refreshToken);
            if (!"refresh".equals(jwt.getClaimAsString("type"))) {
                throw new IllegalArgumentException("Invalid refresh token");
            }
            String email = jwt.getSubject();
            UserEntity user = getUser(email);
            // Optionally rotate refresh tokens here
            return jwtService.generateToken(user);
        } catch (JwtException e) {
            throw new IllegalArgumentException("Invalid refresh token");
        }
    }

    @Override
    public void logout(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return;
        }
        String accessToken = authorizationHeader.substring("Bearer ".length());
        blacklistedAccessTokens.add(accessToken);
        try {
            gatewayClient.logout("Bearer " + accessToken);
        } catch (Exception ignored) { }
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
        Page<UserEntity> users = getUsers(firstName, lastName, city, role, pageable);

        if (role != RoleEnum.DOCTOR) {
            return users;
        }

        List<UserEntity> filteredDoctors = users.getContent().stream()
                .map(doctor -> attachDoctorProfile(doctor))
                .filter(Objects::nonNull)
                .filter(doctor -> matchesSpecialization(doctor.getDoctorProfile(), specialization))
                .toList();

        return new PageImpl<>(filteredDoctors, pageable, filteredDoctors.size());
    }

    private UserEntity attachDoctorProfile(UserEntity doctor) {
        DoctorProfileDtoResponse profile = doctorServiceClient.getDoctorProfile(doctor.getId());
        if (profile != null) {
            doctor.setDoctorProfile(profile);
            return doctor;
        }
        return null;
    }

    private boolean matchesSpecialization(DoctorProfileDtoResponse profile, String specialization) {
        if (specialization == null || specialization.isBlank()) return true;
        return profile.getSpecialty() != null &&
                profile.getSpecialty().toLowerCase().contains(specialization.toLowerCase());
    }


}
