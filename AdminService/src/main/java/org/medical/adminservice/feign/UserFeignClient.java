package org.medical.adminservice.feign;

import org.medical.adminservice.dto.UserDtoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "user-service")
public interface UserFeignClient {
    @GetMapping("/api/user/doctors")
    ResponseEntity<Page<UserDtoResponse>> doctor(
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String specialization,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "firstName,asc") String sort
    );

    @GetMapping("/api/user/doctor/{id}")
    ResponseEntity<UserDtoResponse> doctor(@PathVariable String id);

    @GetMapping("/api/user/patients")
    ResponseEntity<Page<UserDtoResponse>> patient(
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String city,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "createdAt,asc") String sort
    );

    @GetMapping("/api/user/patient/{id}")
    ResponseEntity<UserDtoResponse> patient(@PathVariable String id);


    @RequestMapping(method = RequestMethod.DELETE, path = "/api/user/delete/{id}")
    ResponseEntity<String> deleteAccount(@PathVariable String id);

    @RequestMapping(method = RequestMethod.PUT, path = "/api/user/activate/{id}")
    ResponseEntity<String> activateAccount(@PathVariable String id);

}