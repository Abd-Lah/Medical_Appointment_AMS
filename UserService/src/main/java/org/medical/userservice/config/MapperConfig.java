package org.medical.userservice.config;

import org.mapstruct.factory.Mappers;
import org.medical.userservice.dto.mapper.DoctorMapper;
import org.medical.userservice.dto.mapper.PatientMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MapperConfig {

    @Bean
    public PatientMapper patientMapper() {
        return Mappers.getMapper(PatientMapper.class);
    }

    @Bean
    public DoctorMapper doctorMapper() {
        return Mappers.getMapper(DoctorMapper.class);
    }
}
