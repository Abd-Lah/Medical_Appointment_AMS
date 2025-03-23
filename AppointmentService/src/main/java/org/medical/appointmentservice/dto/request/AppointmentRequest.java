package org.medical.appointmentservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.medical.appointmentservice.dto.response.DoctorProfileResponseDto;
import org.medical.appointmentservice.exception.ForbiddenException;
import org.medical.appointmentservice.exception.InvalidRequestException;
import org.medical.appointmentservice.exception.ValidationException;
import org.medical.appointmentservice.model.AppointmentEntity;
import org.medical.appointmentservice.model.AppointmentStatus;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentRequest {

    public static final Integer APPOINTMENT_AFTER_MINUTES = 30;
    public static final Integer APPOINTMENT_AFTER_DAYS = 15;
    public static final Integer APPOINTMENT_BEFORE_HOURS = 8;
    public static final Integer APPOINTMENT_BEFORE_DAYS = 7;

    @NotBlank(message = "Doctor field is required")
    private String patientId;
    @NotBlank(message = "Doctor field is required")
    private String doctorId;

    @NotBlank(message = "Appointment date is required")
    private LocalDateTime appointmentDate;

    /*
        1/ Throws a ValidationException if the appointment is already booked.
        2/ Checks if the appointment is at least 30 minutes after the current time: Ensures the user cannot book an appointment immediately.
        3/ Ensures the appointment is scheduled for today or a future date.
        4/ Validates that the selected day is part of the doctor's working days.
        5/ Ensures the selected time falls within the doctor's available working hours and is not during a break.
    */
    public void validate(DoctorProfileResponseDto doctorProfileDTO , Boolean alreadyTaken, Boolean hasPendingAppointment, Boolean canceled) throws ValidationException {

        LocalDateTime now = LocalDateTime.now().plusMinutes(APPOINTMENT_AFTER_MINUTES);


        String workingDays = doctorProfileDTO.getWorkingDays();
        LocalTime startTime = LocalTime.parse(doctorProfileDTO.getStartTime());
        LocalTime endTime = LocalTime.parse(doctorProfileDTO.getEndTime());
        int appointmentDuration = doctorProfileDTO.getAppointmentDuration();
        LocalTime breakStartTime = LocalTime.parse(doctorProfileDTO.getBreakTimeStart());
        LocalTime breakEndTime = LocalTime.parse(doctorProfileDTO.getBreakTimeEnd());
        if(canceled){
            throw new ForbiddenException("Too many canceled appointments.");
        }
        if(appointmentDate.isAfter(now.plusDays(APPOINTMENT_AFTER_DAYS)))
            throw new ForbiddenException("Choose date in range of 15 days. Try later !");
        if(hasPendingAppointment){
            throw new ValidationException("You have already pending appointment.");
        }
        if(alreadyTaken) {
            throw new ValidationException("Appointment already taken.");
        }
        if (appointmentDate.isBefore(now)) {
            throw new ValidationException("Appointment cannot be booked . It must be at least 30 minutes after the current time.");
        }

        if (appointmentDate.toLocalDate().isBefore(LocalDate.now()))
            throw new ValidationException("Appointment date cannot be in the past.");

        if (!isWorkingDay(appointmentDate, workingDays)) {
            throw new ValidationException("The selected date is not a working day for the doctor.");
        }

        String appointmentTime = appointmentDate.toLocalTime().toString();
        List<String> availableTimes = generateAvailableTimes(startTime, endTime, breakStartTime, breakEndTime, appointmentDuration);

        if (!availableTimes.contains(appointmentTime)) {
            throw new ValidationException("The selected time is outside the available working hours or during a break.");
        }
    }

    public void validateInUpdate(AppointmentEntity existingAppointment, DoctorProfileResponseDto doctorProfileDto, Boolean alreadyTaken, Boolean hasPendingAppointment) {

        // check if updated before
        if(!existingAppointment.getCreatedAt().isEqual(existingAppointment.getUpdatedAt())) {
            throw new InvalidRequestException("You cannot update an appointment more than once");
        }
        // only pending and upcoming appointment allows to update
        if(!existingAppointment.getStatus().equals(AppointmentStatus.PENDING) || existingAppointment.getAppointmentDate().isBefore(LocalDateTime.now())) {
            throw new InvalidRequestException("Only pending and upcoming appointment allows to update");
        }

        // Don't allow changes if not before 8 hours
        if(!existingAppointment.getAppointmentDate().isAfter(LocalDateTime.now().plusHours(APPOINTMENT_BEFORE_HOURS))) {
            throw new InvalidRequestException("Unfortunately you cannot update this appointment because it still less than 8 hours");
        }
        // the appointment date should be changed
        if(existingAppointment.getAppointmentDate().equals(this.getAppointmentDate())) {
            throw new InvalidRequestException("Appointment date cannot be the same");
        }
        this.validate(doctorProfileDto, alreadyTaken, hasPendingAppointment,false);
    }

    private boolean isWorkingDay(LocalDateTime appointmentDate, String workingDays) {
        DayOfWeek dayOfWeek = appointmentDate.getDayOfWeek();

        List<String> workingDayList = Arrays.asList(workingDays.toLowerCase().split(","));

        return workingDayList.contains(dayOfWeek.toString().toLowerCase());
    }

    private List<String> generateAvailableTimes(LocalTime startTime, LocalTime endTime, LocalTime breakStartTime, LocalTime breakEndTime, int appointmentDuration) {
        List<String> availableTimes = new ArrayList<>();
        LocalTime currentTime = startTime;

        while (!currentTime.isAfter(endTime.minusMinutes(appointmentDuration))) { // Ensure there's enough time for appointment duration
            if (currentTime.isBefore(breakStartTime) || currentTime.isAfter(breakEndTime)) {
                availableTimes.add(currentTime.toString());
            }
            currentTime = currentTime.plusMinutes(appointmentDuration); // Increment by appointment duration
        }

        return availableTimes;
    }


}

