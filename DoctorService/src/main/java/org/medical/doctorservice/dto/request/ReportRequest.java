package org.medical.doctorservice.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReportRequest {

    private String doctorId;
    private String diagnosis;
    private String treatment;
    private String notes;
}
