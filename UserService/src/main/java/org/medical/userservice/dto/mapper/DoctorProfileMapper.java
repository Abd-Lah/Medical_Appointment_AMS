package org.medical.userservice.dto.mapper;

import org.mapstruct.Mapper;
import org.medical.userservice.dto.response.DoctorProfileDtoResponse;

@Mapper(componentModel = "spring")
public interface DoctorProfileMapper {
    DoctorProfileDtoResponse toDto(DoctorProfileDtoResponse doctorProfileDtoResponse);
}
