package org.medical.adminservice.controller;

import lombok.RequiredArgsConstructor;
import org.medical.adminservice.dto.UserDtoResponse;
import org.medical.adminservice.service.AdminService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

    @GetMapping(path = "/users/{role}")
    public ResponseEntity<Page<UserDtoResponse>> getUsers(@PathVariable String role) {
        return adminService.getUsers(role);
    }

    @GetMapping(path = "/users/search-doctors")
    public ResponseEntity<Page<UserDtoResponse>> searchDoctors(@RequestParam(required = false) String firstName,
          @RequestParam(required = false) String lastName,
          @RequestParam(required = false) String city,
          @RequestParam(required = false) String specialization,
          @RequestParam(required = false, defaultValue = "0") int page,
          @RequestParam(required = false, defaultValue = "10") int size,
          @RequestParam(required = false, defaultValue = "firstName,asc") String sort
    ) {
        return adminService.searchDoctors(firstName, lastName, city, specialization, page, size, sort);
    }

    @GetMapping(path = "/users/doctor/{id}")
    public ResponseEntity<UserDtoResponse> getDoctor(@PathVariable String id) {
        return adminService.getDoctor(id);
    }

    @GetMapping(path = "/users/search-patients")
    public ResponseEntity<Page<UserDtoResponse>> searchPatient(@RequestParam(required = false) String firstName,
         @RequestParam(required = false) String lastName,
         @RequestParam(required = false) String city,
         @RequestParam(required = false, defaultValue = "0") int page,
         @RequestParam(required = false, defaultValue = "10") int size,
         @RequestParam(required = false, defaultValue = "firstName,asc") String sort
    ){
        return adminService.searchPatients(firstName, lastName, city, page, size, sort);
    }

    @GetMapping(path = "/users/patient/{id}")
    public ResponseEntity<UserDtoResponse> getPatient(@PathVariable String id) {
        return adminService.getPatient(id);
    }

    @DeleteMapping(path = "/users/delete/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable String id) {
        return adminService.deleteUser(id);
    }

    @DeleteMapping(path = "/users/activate/{id}")
    public ResponseEntity<String> validateUser(@PathVariable String id) {
        return adminService.validateDoctor(id);
    }


}
