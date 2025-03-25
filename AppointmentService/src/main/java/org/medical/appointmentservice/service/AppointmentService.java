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
    AppointmentPatientResponseDto getPatientAppointment(String appointmentId, String patientId);
    AppointmentPatientResponseDto makeAppointment(AppointmentRequest appointmentRequest);
    AppointmentPatientResponseDto updateAppointment(String appointmentId, AppointmentRequest appointmentRequest);
    String cancelAppointment(String appointmentId, String patientId);
    void appointmentBill(AppointmentEntity appointment, PatientResponseDto patient, DoctorResponseDto doctor) ;
    Resource getMyAppointmentBill(String appointmentId, String patientId);

    Page<AppointmentDoctorResponseDto> getDoctorAppointments(String userId, String orderBy, Pageable pageable);
    AppointmentDoctorResponseDto getDoctorAppointment(String appointmentId, String doctorId);
    AppointmentDoctorResponseDto changeStatus(String doctorId, AppointmentStatus status, String appointmentId);
    ReportResponseDto addReportToAppointment(ReportRequest reportCommand, String appointmentId);
    ReportResponseDto editReportAppointment(ReportRequest reportCommand, String reportId);
}
