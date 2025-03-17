package org.medical.userservice.controller;

import lombok.RequiredArgsConstructor;
import org.medical.userservice.dto.mapper.DoctorMapper;
import org.medical.userservice.dto.request.DoctorProfileRequest;
import org.medical.userservice.dto.response.DoctorDtoResponse;
import org.medical.userservice.service.DoctorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/doctor")
@RequiredArgsConstructor
public class DoctorController {
    private final DoctorService doctorService;

    @PutMapping("/update/{id}")
    public ResponseEntity<DoctorDtoResponse> updateUser(@PathVariable String id, @RequestBody DoctorProfileRequest doctorProfileRequest) {
        return new ResponseEntity<>(DoctorMapper.INSTANCE.toDto(doctorService.updateProfile(id,doctorProfileRequest)), HttpStatus.OK);
    }
}
