package org.medical.userservice.controller;

import lombok.RequiredArgsConstructor;
import org.medical.userservice.dto.mapper.DoctorMapper;
import org.medical.userservice.dto.request.RegisterRequest;
import org.medical.userservice.dto.request.UserRequest;
import org.medical.userservice.dto.response.DoctorDtoResponse;
import org.medical.userservice.model.UserEntity;
import org.medical.userservice.service.UserService;
import org.medical.userservice.service.factory.UserRoleMapperFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserRoleMapperFactory userRoleMapperFactory;





    @GetMapping("/all_doctors")
    public ResponseEntity<Page<DoctorDtoResponse>> doctor(
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String specialization,
            Pageable pageable) {

        Page<UserEntity> usersPage = userService.getAllDoctors(firstName, lastName, city, specialization, pageable);

        return new ResponseEntity<>(DoctorMapper.INSTANCE.toDtoPage(usersPage), HttpStatus.OK);
    }


    @GetMapping("/doctor/{id}")
    public ResponseEntity<DoctorDtoResponse> doctor(@PathVariable String id) {
        UserEntity user = userService.getDoctor(id);
        return new ResponseEntity<>(DoctorMapper.INSTANCE.toDto(user), HttpStatus.OK);
    }

    @PostMapping(path = "/user/create")
    public ResponseEntity<?> createUser(@RequestBody RegisterRequest userRequest) {
        UserEntity user = userService.createUser(userRequest);
        Object dto = userRoleMapperFactory.getMapper(user.getRole(), user);
        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }

    @PutMapping("/user/update/{id}")
    public ResponseEntity<?> updateUser(@PathVariable String id, @RequestBody UserRequest userRequest) {

        UserEntity updatedUser = userService.updateProfile(id, userRequest);

        Object dto = userRoleMapperFactory.getMapper(updatedUser.getRole(), updatedUser);

        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @PatchMapping(path = "/user/delete/{id}")
    public ResponseEntity<String> deleteAccount(@PathVariable String id) {
        userService.deleteAccount(id);
        return new ResponseEntity<>("Your account was deleted successfully",HttpStatus.OK);
    }



}
