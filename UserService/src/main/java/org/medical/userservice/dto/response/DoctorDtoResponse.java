package org.medical.userservice.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DoctorDtoResponse {

    private String id;

    private String email;

    private String firstName;

    private String lastName;

    private String city;

    private String phone;

    private DoctorProfileDtoResponse doctorProfile ;
}

