package org.medical.doctorservice.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.medical.doctorservice.dto.response.DoctorProfileDtoResponse;
import org.medical.doctorservice.model.DoctorProfileEntity;

@Mapper
public interface DoctorProfileMapper {
    DoctorProfileMapper INSTANCE = Mappers.getMapper(DoctorProfileMapper.class);

    DoctorProfileDtoResponse toDoctorProfileDto(DoctorProfileEntity doctorProfile);
}