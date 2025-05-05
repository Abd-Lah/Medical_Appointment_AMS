package org.medical.userservice.controller;

import lombok.RequiredArgsConstructor;
import org.medical.userservice.dto.mapper.DoctorMapper;
import org.medical.userservice.dto.mapper.PatientMapper;
import org.medical.userservice.dto.request.AuthRequest;
import org.medical.userservice.dto.request.RegisterRequest;
import org.medical.userservice.dto.request.UserRequest;
import org.medical.userservice.dto.response.AuthResponse;
import org.medical.userservice.dto.response.DoctorDtoResponse;
import org.medical.userservice.dto.response.PatientDtoResponse;
import org.medical.userservice.model.RoleEnum;
import org.medical.userservice.model.UserEntity;
import org.medical.userservice.service.JwtService;
import org.medical.userservice.service.UserService;
import org.medical.userservice.service.factory.UserRoleMapperFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(path = "/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserRoleMapperFactory userRoleMapperFactory;
    private final AuthenticationManager authManager;
    private final JwtService jwtService;

    @GetMapping("/doctors")
    public ResponseEntity<Page<DoctorDtoResponse>> doctor(
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String specialization,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "firstName,asc") String sort
            ) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.asc(sort.split(",")[0])));

        Page<UserEntity> usersPage = userService.getAllDoctors(firstName, lastName, city, specialization, pageable);

        return new ResponseEntity<>(DoctorMapper.INSTANCE.toDtoPage(usersPage), HttpStatus.OK);
    }


    @GetMapping("/doctor/{id}")
    public ResponseEntity<DoctorDtoResponse> doctor(@PathVariable String id) {
        UserEntity user = userService.getDoctor(id);
        return new ResponseEntity<>(DoctorMapper.INSTANCE.toDto(user), HttpStatus.OK);
    }

    @GetMapping("/patients")
    public ResponseEntity<Page<PatientDtoResponse>> patient(
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String city,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "createdAt,asc") String sort
    ) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.asc(sort.split(",")[0])));

        Page<UserEntity> usersPage = userService.getAllPatients(firstName, lastName, city, pageable);

        return new ResponseEntity<>(PatientMapper.INSTANCE.toDtoPage(usersPage), HttpStatus.OK);
    }


    @GetMapping("/patient/{id}")
    public ResponseEntity<PatientDtoResponse> patient(@PathVariable String id) {
        UserEntity user = userService.getPatient(id);
        return new ResponseEntity<>(PatientMapper.INSTANCE.toDto(user), HttpStatus.OK);
    }

    @PostMapping(path = "/create")
    public ResponseEntity<?> createUser(@RequestBody RegisterRequest userRequest) {
        UserEntity user = userService.createUser(userRequest);

        String token = jwtService.generateToken(user);

        Object dto = userRoleMapperFactory.getMapper(user.getRole(), user);

        AuthResponse authResponse = new AuthResponse(dto, token);
        return new ResponseEntity<>(authResponse, HttpStatus.CREATED);
    }


    @PostMapping(path = "/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest loginRequest) {
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        UserEntity user = userService.getUser(loginRequest.getEmail());

        String token = jwtService.generateToken(user);

        Object dto = userRoleMapperFactory.getMapper(user.getRole(), user);

        AuthResponse authResponse = new AuthResponse(dto, token);
        return new ResponseEntity<>(authResponse, HttpStatus.OK);
    }


    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateUser(@PathVariable String id, @RequestBody UserRequest userRequest) {

        UserEntity updatedUser = userService.update(id, userRequest);

        Object dto = userRoleMapperFactory.getMapper(updatedUser.getRole(), updatedUser);

        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @DeleteMapping(path = "/delete/{id}")
    public ResponseEntity<String> deleteAccount(@PathVariable String id) {
        userService.deleteAccount(id);
        return new ResponseEntity<>("Your account was deleted successfully",HttpStatus.OK);
    }

    @PutMapping(path = "/activate/{id}")
    public ResponseEntity<String> activateAccount(@PathVariable String id) {
        userService.activateAccount(id);
        return new ResponseEntity<>("Your account was activated successfully",HttpStatus.OK);
    }



}
