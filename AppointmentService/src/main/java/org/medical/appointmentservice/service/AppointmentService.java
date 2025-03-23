package org.medical.appointmentservice.service;

import org.medical.appointmentservice.dto.request.AppointmentRequest;
import org.medical.appointmentservice.dto.request.ReportRequest;
import org.medical.appointmentservice.dto.response.*;
import org.medical.appointmentservice.model.AppointmentEntity;
import org.medical.appointmentservice.model.AppointmentStatus;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AppointmentService {
    Page<AppointmentPatientResponseDto> getPatientAppointments(String userId, String orderBy, Pageable pageable);
    AppointmentPatientResponseDto getPatientAppointment(String appointmentId);
    AppointmentPatientResponseDto makeAppointment(AppointmentRequest appointmentRequest);
    AppointmentPatientResponseDto updateAppointment(String appointmentId, AppointmentRequest appointmentRequest);
    String cancelAppointment(String appointmentId);
    void createAppointmentBill(AppointmentEntity appointment, PatientResponseDto patient, DoctorResponseDto doctor) ;
    Resource getMyAppointmentBill(String appointmentId);

    Page<AppointmentDoctorResponseDto> getDoctorAppointments(String userId, Pageable pageable);
    AppointmentDoctorResponseDto getDoctorAppointment(String appointmentId);
    AppointmentDoctorResponseDto changeStatus(AppointmentStatus status, String appointmentId);
    ReportResponseDto addReportToAppointment(ReportRequest reportCommand, String appointmentId);
    ReportResponseDto editReportAppointment(ReportRequest reportCommand, String reportId);
}
