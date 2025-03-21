package org.medical.userservice.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.medical.userservice.dto.response.DoctorDtoResponse;
import org.medical.userservice.model.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;
import java.util.stream.Collectors;

@Mapper  (uses = DoctorProfileMapper.class)// Ensure DoctorProfileMapper is used for nested mapping
public interface DoctorMapper extends UserMapper {
    DoctorMapper INSTANCE = Mappers.getMapper(DoctorMapper.class);

    @Override
    @Mapping(target = "doctorProfile", source = "doctorProfile") // Map the doctorProfile from UserEntity to DoctorDtoResponse
    DoctorDtoResponse toDto(UserEntity userEntity);

    @Override
    default Page<DoctorDtoResponse> toDtoPage(Page<UserEntity> userEntitiesPage) {
        List<DoctorDtoResponse> doctorDtos = userEntitiesPage.getContent().stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        return new PageImpl<>(doctorDtos, userEntitiesPage.getPageable(), userEntitiesPage.getTotalElements());
    }
}