package org.medical.userservice.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.medical.userservice.dto.response.DoctorProfileDtoResponse;
import org.medical.userservice.model.DoctorProfileEntity;

@Mapper
public interface DoctorProfileMapper {
    DoctorProfileMapper INSTANCE = Mappers.getMapper(DoctorProfileMapper.class);

    DoctorProfileDtoResponse toDoctorProfileDto(DoctorProfileEntity doctorProfile);
}