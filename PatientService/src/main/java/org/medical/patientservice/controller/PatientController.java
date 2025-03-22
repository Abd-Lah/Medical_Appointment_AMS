package org.medical.patientservice.controller;

import lombok.RequiredArgsConstructor;
import org.medical.patientservice.dto.request.UserRequest;
import org.medical.patientservice.dto.response.DoctorResponseDto;
import org.medical.patientservice.dto.response.UserResponseDto;
import org.medical.patientservice.service.PatientService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/patient")
@RequiredArgsConstructor
public class PatientController {
    private final PatientService patientService;

    @GetMapping("/doctors")
    public ResponseEntity<Page<DoctorResponseDto>> getDoctors( @RequestParam(required = false) String firstName,
       @RequestParam(required = false) String lastName,
       @RequestParam(required = false) String city,
       @RequestParam(required = false) String specialization,
       @RequestParam(required = false, defaultValue = "0") int page,
       @RequestParam(required = false, defaultValue = "10") int size,
       @RequestParam(required = false, defaultValue = "firstName,asc") String sort)
    {
        return patientService.getDoctors(firstName,lastName,city,specialization,page,size,sort);
    }

    @GetMapping("/doctor/{id}")
    public ResponseEntity<DoctorResponseDto> getDoctor(@PathVariable String id) {
        return patientService.getDoctor(id);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<UserResponseDto> update(@PathVariable("id") String id, @RequestBody UserRequest userRequest) {
        return patientService.updateUser(id, userRequest);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable("id") String id) {
        return patientService.deleteUser(id);
    }

    @PutMapping("/activate/{id}")
    public ResponseEntity<String> activate(@PathVariable("id") String id) {
        return patientService.activateUser(id);
    }
}
