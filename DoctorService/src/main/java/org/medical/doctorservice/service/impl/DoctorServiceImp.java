package org.medical.doctorservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.medical.doctorservice.dto.request.DoctorProfileRequest;
import org.medical.doctorservice.exception.InvalidRequestException;
import org.medical.doctorservice.feign.UserFeignClient;
import org.medical.doctorservice.model.DoctorProfileEntity;
import org.medical.doctorservice.repository.DoctorProfileRepository;
import org.medical.doctorservice.service.DoctorService;
import org.medical.doctorservice.util.FeignResponseHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DoctorServiceImp implements DoctorService {
    private final DoctorProfileRepository drp;
    private final UserFeignClient userFeignClient;
    private final FeignResponseHelper helper;

    @Override
    public DoctorProfileEntity getProfile(String id) {
        return drp.getDoctorProfileByDoctorId(id);
    }

    @Override
    @Transactional
    public DoctorProfileEntity createProfile(DoctorProfileRequest request) {
        // checking if really doctor;
        DoctorProfileEntity profile = request.createProfile();
        return drp.save(profile);
    }

    @Override
    @Transactional
    public DoctorProfileEntity updateProfile(String id, DoctorProfileRequest request) {
        helper.checkResponseStatus(userFeignClient.getDoctor(request.getDoctorId()));
        DoctorProfileEntity profile = drp.getDoctorProfileByDoctorId(id);
        if(profile.getDoctorId().equals(request.getDoctorId())){
            profile.updateDoctorProfile(request);
            return drp.save(profile);
        }else{
            throw new InvalidRequestException("Invalid request");
        }
    }

    @Override
    @Transactional
    public void deleteProfile(String id) {
        DoctorProfileEntity profile = drp.getDoctorProfileByDoctorId(id);
        profile.setDeleted(true);
        drp.save(profile);
    }

    @Override
    public void activateProfile(String id) {
        DoctorProfileEntity profile = drp.getDoctorProfileByDoctorId(id);
        profile.setDeleted(false);
        drp.save(profile);
    }
}
