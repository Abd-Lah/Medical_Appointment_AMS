package org.medical.userservice.controller;

import lombok.RequiredArgsConstructor;
import org.medical.userservice.dto.mapper.DoctorMapper;
import org.medical.userservice.dto.response.DoctorDtoResponse;
import org.medical.userservice.model.RoleEnum;
import org.medical.userservice.model.UserEntity;
import org.medical.userservice.service.AdminService;
import org.medical.userservice.service.factory.UserRoleMapperFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "api/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;
    private final UserRoleMapperFactory userRoleMapperFactory;

    @GetMapping("/all/{search}")
    public ResponseEntity<Page<?>> users(@PathVariable String search, Pageable pageable) {
        Page<UserEntity> users = adminService.getAllUsersByRole(search, pageable);
        Page<?> mappedPage = userRoleMapperFactory.getMapper(RoleEnum.valueOf(search.toUpperCase()), users);
        return ResponseEntity.ok(mappedPage);
    }

    @GetMapping("/all_doctors")
    public ResponseEntity<Page<DoctorDtoResponse>> doctor(
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String specialization,
            Pageable pageable) {

        Page<UserEntity> usersPage = adminService.search(firstName, lastName, city, specialization, pageable);

        return new ResponseEntity<>(DoctorMapper.INSTANCE.toDtoPage(usersPage), HttpStatus.OK);
    }

    @PatchMapping("/delete_user/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable String id) {
        boolean delete = adminService.deleteUser(id);
        return ResponseEntity.ok("User "+(delete ? "activated" : "inactivated" )+" successfully");
    }

    @PatchMapping("/validate_doctor/{id}")
    public ResponseEntity<String> validateDoctor(@PathVariable String id) {
        boolean validate = adminService.validateDoctor(id);
        return ResponseEntity.ok("Doctor "+(validate ? "validated" : "unvalidated" )+" successfully");
    }

}
