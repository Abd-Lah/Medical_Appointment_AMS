package org.medical.patientservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDto {

    private String id;

    private String firstName;

    private String lastName;

    private String city;

    private String email;

    private String phone;
}
