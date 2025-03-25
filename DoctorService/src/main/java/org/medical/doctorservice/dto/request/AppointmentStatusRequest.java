package org.medical.doctorservice.dto.request;

import org.medical.doctorservice.dto.response.AppointmentStatusDto;
import org.medical.doctorservice.exception.ValidationException;

public class AppointmentStatusRequest {
    private String status;

    public AppointmentStatusDto validate() {
        if (status == null || status.isEmpty()) {
            throw new ValidationException("Status is required");
        }
        if(!status.equalsIgnoreCase("pending") && !status.equalsIgnoreCase("approved") && !status.equalsIgnoreCase("cancelled")){
            throw new ValidationException("Status must be 'pending' or 'approved' or 'cancelled'");
        }
        return AppointmentStatusDto.valueOf(status.toUpperCase());
    }
}
