package org.medical.patientservice;

import feign.codec.ErrorDecoder;
import org.medical.patientservice.feign.response.CustomErrorDecoder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableFeignClients
public class PatientServiceApplication {

    public static void main(String[] args) {

        SpringApplication.run(PatientServiceApplication.class, args);
    }

    @Bean
    public ErrorDecoder errorDecoder() {
        return new CustomErrorDecoder();
    }

}
