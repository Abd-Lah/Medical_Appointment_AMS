package org.medical.userservice.dto.mapper;


import org.medical.userservice.model.UserEntity;
import org.springframework.data.domain.Page;

public interface UserMapper {
    Object toDto(UserEntity userEntity);
    Page<?> toDtoPage(Page<UserEntity> userEntity);
}