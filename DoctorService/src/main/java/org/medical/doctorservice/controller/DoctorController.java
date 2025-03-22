package org.medical.doctorservice.controller;

import lombok.RequiredArgsConstructor;
import org.medical.doctorservice.dto.mapper.DoctorProfileMapper;
import org.medical.doctorservice.dto.request.DoctorProfileRequest;
import org.medical.doctorservice.dto.response.DoctorProfileDtoResponse;
import org.medical.doctorservice.model.DoctorProfileEntity;
import org.medical.doctorservice.service.DoctorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/doctors")
@RequiredArgsConstructor
public class DoctorController {
    private final DoctorService doctorService;

    @GetMapping(path = "/{id}")
    public DoctorProfileDtoResponse getDoctorProfile(@PathVariable String id) {
        DoctorProfileEntity profile = doctorService.getProfile(id);
        return DoctorProfileMapper.INSTANCE.toDoctorProfileDto(profile);
    }

    @PostMapping(path = "/profile/create")
    public DoctorProfileDtoResponse createDoctorProfile(@RequestBody DoctorProfileRequest request){
        return DoctorProfileMapper.INSTANCE.toDoctorProfileDto(doctorService.createProfile(request));
    }

    @PutMapping(path = "/profile/update/{id}")
    public DoctorProfileDtoResponse updateDoctorProfile(@PathVariable String id, @RequestBody DoctorProfileRequest request){
        return DoctorProfileMapper.INSTANCE.toDoctorProfileDto(doctorService.updateProfile(id,request));
    }

    @DeleteMapping(path = "/profile/delete/{id}")
    public ResponseEntity<Void> deleteDoctorProfile(@PathVariable String id) {
        doctorService.deleteProfile(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping(path = "/profile/activate/{id}")
    public ResponseEntity<Void> activateDoctorProfile(@PathVariable String id) {
        doctorService.activateProfile(id);
        return ResponseEntity.noContent().build();
    }
}
