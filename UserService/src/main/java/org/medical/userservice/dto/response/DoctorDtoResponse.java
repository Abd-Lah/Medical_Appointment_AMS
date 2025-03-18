package org.medical.userservice.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DoctorDtoResponse {

    private String id;

    private String firstName;

    private String lastName;

    private String city;

    private DoctorProfileDtoResponse doctorProfileDto ;
}

