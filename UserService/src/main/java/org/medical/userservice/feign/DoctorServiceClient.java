package org.medical.userservice.feign;

import org.medical.userservice.dto.request.DoctorProfileRequest;
import org.medical.userservice.dto.response.DoctorProfileDtoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "doctor-service")
public interface DoctorServiceClient {
    @GetMapping("/api/doctors/{doctorId}")
    DoctorProfileDtoResponse getDoctorProfile(@PathVariable("doctorId") String doctorId);

    @PostMapping("/api/doctors/profile/create")
    DoctorProfileDtoResponse createDoctorProfile(@RequestBody DoctorProfileRequest request);

    @PutMapping(path = "api/doctors/profile/update/{id}")
    DoctorProfileDtoResponse updateDoctorProfile(@PathVariable("id") String id, @RequestBody DoctorProfileRequest request);

    @DeleteMapping(path = "/api/doctors/profile/delete/{id}")
    void deleteDoctorProfile(@PathVariable("id") String id);

    @PutMapping(path = "/api/doctors/profile/activate/{id}")
    void activateDoctorProfile(@PathVariable("id") String id);
}
