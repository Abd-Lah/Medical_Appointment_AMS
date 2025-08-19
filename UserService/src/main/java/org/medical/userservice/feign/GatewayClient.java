package org.medical.userservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "gateway-service")
public interface GatewayClient {

    @PostMapping("/auth/logout")
    ResponseEntity<Void> logout(@RequestHeader("Authorization") String authorization);
}


