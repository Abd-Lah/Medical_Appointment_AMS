package org.medical.doctorservice.dto.request;

import lombok.Getter;
import lombok.Setter;
import org.medical.doctorservice.dto.response.AppointmentStatusDto;
import org.medical.doctorservice.exception.ValidationException;

@Getter
@Setter
public class AppointmentStatusRequest {
    private String Status;

    public AppointmentStatusDto validate() {
        if (Status == null || Status.isEmpty()) {
            throw new ValidationException("Status is required");
        }
        if(!Status.equalsIgnoreCase("pending") && !Status.equalsIgnoreCase("approved") && !Status.equalsIgnoreCase("cancelled")){
            throw new ValidationException("Status must be 'pending' or 'approved' or 'cancelled'");
        }
        return AppointmentStatusDto.valueOf(Status.toUpperCase());
    }
}
