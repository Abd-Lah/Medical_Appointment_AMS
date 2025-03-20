package org.medical.doctorservice.service;

import org.medical.doctorservice.dto.request.DoctorProfileRequest;
import org.medical.doctorservice.model.DoctorProfileEntity;

public interface DoctorService {
    DoctorProfileEntity getProfile(String id);
    DoctorProfileEntity createProfile(DoctorProfileRequest request);
    DoctorProfileEntity updateProfile(String id, DoctorProfileRequest profile);
    void deleteProfile(String id);
    void activateProfile(String id);
}
