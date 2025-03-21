package org.medical.adminservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDtoResponse {

    private String id;

    private String firstName;

    private String lastName;

    private String city;

    private DoctorProfileDtoResponse doctorProfile ;
}
