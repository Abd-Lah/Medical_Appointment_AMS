package org.medical.appointmentservice.feign;

import org.medical.appointmentservice.dto.response.DoctorResponseDto;
import org.medical.appointmentservice.dto.response.PatientResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "User-Service", path = "/api/user")
public interface UserFeignClient {
    @GetMapping("/doctor/{id}")
    ResponseEntity<DoctorResponseDto> getDoctorWithProfile(@PathVariable String id);
    @GetMapping("/patient/{id}")
    ResponseEntity<PatientResponseDto> getPatient(@PathVariable String id);
}
