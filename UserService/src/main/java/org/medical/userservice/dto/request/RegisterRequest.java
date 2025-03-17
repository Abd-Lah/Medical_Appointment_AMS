package org.medical.userservice.dto.request;

import lombok.Data;
import org.medical.userservice.model.RoleEnum;
import org.medical.userservice.model.UserEntity;

@Data
public class RegisterRequest {

    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String city;
    private RoleEnum role;

    public UserEntity toUserEntity() {
        return new UserEntity(
                email,
                password,
                firstName,
                lastName,
                phoneNumber,
                city,
                role
        );
    }
}