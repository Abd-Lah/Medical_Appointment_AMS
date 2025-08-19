package org.medical.userservice.controller;

import lombok.RequiredArgsConstructor;
import org.medical.userservice.dto.mapper.DoctorMapper;
import org.medical.userservice.dto.mapper.PatientMapper;
import org.medical.userservice.dto.request.AuthRequest;
import org.medical.userservice.dto.request.RefreshRequest;
import org.medical.userservice.dto.request.RegisterRequest;
import org.medical.userservice.dto.request.UserRequest;
import org.medical.userservice.dto.response.AuthResponse;
import org.medical.userservice.dto.response.DoctorDtoResponse;
import org.medical.userservice.dto.response.PatientDtoResponse;
import org.medical.userservice.model.RoleEnum;
import org.medical.userservice.model.UserEntity;
import org.medical.userservice.service.SimpleJwtService;
import org.medical.userservice.service.UserService;
import org.medical.userservice.service.factory.UserRoleMapperFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    private final SimpleJwtService jwtService;
    private final DoctorMapper doctorMapper;
    private final PatientMapper patientMapper;

    @GetMapping("/doctors")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR','PATIENT')")
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

        return new ResponseEntity<>(doctorMapper.toDtoPage(usersPage), HttpStatus.OK);
    }


    @GetMapping("/doctor/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR','PATIENT')")
    public ResponseEntity<DoctorDtoResponse> doctor(@PathVariable String id) {
        UserEntity user = userService.getDoctor(id);
        return new ResponseEntity<>(doctorMapper.toDto(user), HttpStatus.OK);
    }

    @GetMapping("/patients")
    @PreAuthorize("hasRole('ADMIN')")
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

        return new ResponseEntity<>(patientMapper.toDtoPage(usersPage), HttpStatus.OK);
    }


    @GetMapping("/patient/{id}")
    @PreAuthorize("@authorizationChecker.isOwner(#id) or hasRole('ADMIN')")
    public ResponseEntity<PatientDtoResponse> patient(@PathVariable String id) {
        UserEntity user = userService.getPatient(id);
        return new ResponseEntity<>(patientMapper.toDto(user), HttpStatus.OK);
    }

    @PostMapping(path = "/create")
    public ResponseEntity<?> createUser(@RequestBody RegisterRequest userRequest) {
        UserEntity user = userService.createUser(userRequest);

        String token = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        Object dto = userRoleMapperFactory.getMapper(user.getRole(), user);

        AuthResponse authResponse = new AuthResponse(dto, token, refreshToken);
        return new ResponseEntity<>(authResponse, HttpStatus.CREATED);
    }


    @PostMapping(path = "/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest loginRequest) {
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        UserEntity user = userService.getUser(loginRequest.getEmail());

        String token = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        Object dto = userRoleMapperFactory.getMapper(user.getRole(), user);

        AuthResponse authResponse = new AuthResponse(dto, token, refreshToken);
        return new ResponseEntity<>(authResponse, HttpStatus.OK);
    }

    @PostMapping(path = "/refresh")
    public ResponseEntity<?> refresh(@RequestBody RefreshRequest request) {
        String newAccessToken = userService.refreshAccessToken(request.getRefreshToken());
        return new ResponseEntity<>(newAccessToken, HttpStatus.OK);
    }

    @PostMapping(path = "/logout")
    public ResponseEntity<?> logout(@RequestHeader(name = "Authorization", required = false) String authorizationHeader) {
        userService.logout(authorizationHeader);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    @PutMapping("/update/{id}")
    @PreAuthorize("@authorizationChecker.isOwner(#id) or hasRole('ADMIN')")
    public ResponseEntity<?> updateUser(@PathVariable String id, @RequestBody UserRequest userRequest) {

        UserEntity updatedUser = userService.update(id, userRequest);

        Object dto = userRoleMapperFactory.getMapper(updatedUser.getRole(), updatedUser);

        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @DeleteMapping(path = "/delete/{id}")
    @PreAuthorize("@authorizationChecker.isOwner(#id) or hasRole('ADMIN')")
    public ResponseEntity<String> deleteAccount(@PathVariable String id) {
        userService.deleteAccount(id);
        return new ResponseEntity<>("Your account was deleted successfully",HttpStatus.OK);
    }

    @PutMapping(path = "/activate/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> activateAccount(@PathVariable String id) {
        userService.activateAccount(id);
        return new ResponseEntity<>("Your account was activated successfully",HttpStatus.OK);
    }

    @GetMapping("/test-jwt")
    public ResponseEntity<String> testJwt() {
        try {
            // Create a test user for JWT generation
            UserEntity testUser = new UserEntity();
            testUser.setId("test-id");
            testUser.setEmail("test@example.com");
            testUser.setRole(RoleEnum.PATIENT);

            String token = jwtService.generateToken(testUser);
            return new ResponseEntity<>("JWT generated successfully: " + token.substring(0, 50) + "...", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("JWT generation failed: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
