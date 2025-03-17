package org.medical.userservice.service.factory;

import org.medical.userservice.dto.mapper.DoctorMapper;
import org.medical.userservice.dto.mapper.PatientMapper;
import org.medical.userservice.dto.mapper.UserMapper;
import org.medical.userservice.exception.ResourceNotFoundException;
import org.medical.userservice.model.RoleEnum;
import org.medical.userservice.model.UserEntity;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class UserRoleMapperFactory {

    private final Map<RoleEnum, UserMapper> roleToMapperMap;
    public UserRoleMapperFactory(ApplicationContext applicationContext) {
        this.roleToMapperMap = Map.of(
                RoleEnum.PATIENT, applicationContext.getBean(PatientMapper.class),
                RoleEnum.DOCTOR, applicationContext.getBean(DoctorMapper.class),
                RoleEnum.ADMIN, applicationContext.getBean(PatientMapper.class)
        );
    }

    public Object getMapper(RoleEnum role, UserEntity userEntity) throws ResourceNotFoundException {
        UserMapper userMapper = roleToMapperMap.get(role);
        if (userMapper == null) {
            throw new ResourceNotFoundException("No such role: " + role);
        }
        return userMapper.toDto(userEntity);
    }

    public Page<?> getMapper(RoleEnum role, Page<UserEntity> userEntities) throws ResourceNotFoundException {
        UserMapper userMapper = roleToMapperMap.get(role);
        if (userMapper == null) {
            throw new ResourceNotFoundException("No such role: " + role);
        }
        return userMapper.toDtoPage(userEntities);
    }
}
