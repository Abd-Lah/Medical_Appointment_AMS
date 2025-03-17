package org.medical.userservice.service;

import org.medical.userservice.dto.request.DoctorProfileRequest;
import org.medical.userservice.model.UserEntity;

public interface DoctorService {
    UserEntity updateProfile(String id, DoctorProfileRequest profile);
}
