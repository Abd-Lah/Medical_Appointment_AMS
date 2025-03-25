package org.medical.patientservice.feign;

import org.medical.patientservice.dto.request.UserRequest;
import org.medical.patientservice.dto.response.DoctorResponseDto;
import org.medical.patientservice.dto.response.UserResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "user-service", path = "/api/user")
public interface UserFeignClient {
    @GetMapping("/doctors")
    ResponseEntity<Page<DoctorResponseDto>> doctor(
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String specialization,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "firstName,asc") String sort
    );

    @GetMapping("/doctor/{id}")
    ResponseEntity<DoctorResponseDto> doctor(@PathVariable String id);

    @PutMapping("/update/{id}")
    ResponseEntity<UserResponseDto> updateUser(@PathVariable String id, @RequestBody UserRequest userRequest);

    @DeleteMapping(path = "/delete/{id}")
    ResponseEntity<String> deleteAccount(@PathVariable String id);

    @PutMapping(path = "/activate/{id}")
    ResponseEntity<String> activateAccount(@PathVariable String id);
}
