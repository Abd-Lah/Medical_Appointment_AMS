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

@Mapper
public interface DoctorMapper extends UserMapper {
    DoctorMapper INSTANCE = Mappers.getMapper(DoctorMapper.class);

    @Override
    @Mapping(target = "doctorProfileDTO", source = "doctorProfile")
    DoctorDtoResponse toDto(UserEntity userEntity);
    // Add this method to map Page<UserEntity> to Page<UserDto>
    @Override
    @Mapping(target = "doctorProfileDTO", source = "doctorProfile")
    default Page<DoctorDtoResponse> toDtoPage(Page<UserEntity> userEntitiesPage) {
        List<DoctorDtoResponse> doctorDtos = userEntitiesPage.getContent().stream()
                .map(this::toDto) // Map each UserEntity to UserDto
                .collect(Collectors.toList());

        return new PageImpl<>(doctorDtos, userEntitiesPage.getPageable(), userEntitiesPage.getTotalElements());
    }
}
