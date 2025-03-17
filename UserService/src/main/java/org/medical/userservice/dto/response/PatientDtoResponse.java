package org.medical.userservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientDtoResponse {

    private String id;

    private String firstName;

    private String lastName;

    private String city;

    private String phoneNumber;

}