package org.medical.appointmentservice.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReportResponseDto {

    private String id;

    private String diagnosis;

    private String treatment;

    private String notes;
}
