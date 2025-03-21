package org.medical.doctorservice.feign;

import org.medical.doctorservice.dto.response.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "user-service")
public interface UserFeignClient {
    @GetMapping(path = "/api/user/doctor/{id}")
    ResponseEntity<UserDto> getDoctor(@PathVariable String id);

    @DeleteMapping(path = "/api/user/delete/{id}")
    ResponseEntity<UserDto> deleteDoctor(@RequestParam("id") String id);
}
