package org.medical.userservice.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.medical.userservice.dto.response.PatientDtoResponse;
import org.medical.userservice.model.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;
import java.util.stream.Collectors;

@Mapper
public interface PatientMapper extends UserMapper{
    PatientMapper INSTANCE = Mappers.getMapper(PatientMapper.class);

    @Mapping(source = "phoneNumber", target = "phone")
    PatientDtoResponse toDto(UserEntity userEntity);
    UserEntity toUserEntity(PatientDtoResponse userDto);
    default Page<PatientDtoResponse> toDtoPage(Page<UserEntity> userEntitiesPage) {
        List<PatientDtoResponse> patientDtos = userEntitiesPage.getContent().stream()
                .map(this::toDto) // Map each UserEntity to UserDto
                .collect(Collectors.toList());

        return new PageImpl<>(patientDtos, userEntitiesPage.getPageable(), userEntitiesPage.getTotalElements());
    }
}
