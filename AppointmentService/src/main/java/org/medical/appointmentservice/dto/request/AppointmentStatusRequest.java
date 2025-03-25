package org.medical.appointmentservice.dto.request;

import lombok.Getter;
import lombok.Setter;
import org.medical.appointmentservice.exception.ValidationException;
import org.medical.appointmentservice.model.AppointmentStatus;

@Getter
@Setter
public class AppointmentStatusRequest {
    private String status;

    public AppointmentStatus validate() {
        if (status == null || status.isEmpty()) {
            throw new ValidationException("Status is required");
        }
        if(!status.equalsIgnoreCase("pending") && !status.equalsIgnoreCase("approved") && !status.equalsIgnoreCase("cancelled")){
            throw new ValidationException("Status must be 'pending' or 'approved' or 'cancelled'");
        }
        return AppointmentStatus.valueOf(status.toUpperCase());
    }
}
