package org.medical.appointmentservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.medical.appointmentservice.dto.request.AppointmentRequest;
import org.medical.appointmentservice.dto.request.ReportRequest;
import org.medical.appointmentservice.dto.response.*;
import org.medical.appointmentservice.feign.UserFeignClient;
import org.medical.appointmentservice.model.AppointmentEntity;
import org.medical.appointmentservice.model.AppointmentStatus;
import org.medical.appointmentservice.model.ReportEntity;
import org.medical.appointmentservice.repository.AppointmentRepository;
import org.medical.appointmentservice.repository.ReportRepository;
import org.medical.appointmentservice.service.AppointmentService;
import org.medical.appointmentservice.util.Helper;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final ReportRepository reportRepository;
    private final UserFeignClient ufc;
    private final PdfGenerationService pdfGenerationService;
    private final Helper<AppointmentEntity> helper;

    @Override
    public Page<AppointmentPatientResponseDto> getPatientAppointments(String userId, String orderBy, Pageable pageable) {
        PatientResponseDto patient = ufc.getPatient(userId).getBody();
        assert patient != null;
        Page<AppointmentEntity> myAppointments = appointmentRepository.getAppointments(patient.getId(), orderBy, pageable);
        return myAppointments.map(this::mapAppointmentToDto);
    }



    @Override
    public AppointmentPatientResponseDto getPatientAppointment(String appointmentId) {
        AppointmentEntity appointment = appointmentRepository.findById(appointmentId).orElse(null);
        helper.isObjectNull(appointment, "Appointment with id " + appointmentId + " not found.");
        assert appointment != null;
        return mapAppointmentToDto(appointment);
    }

    @Override
    @Transactional
    public AppointmentPatientResponseDto makeAppointment(AppointmentRequest appointmentRequest) {
        PatientResponseDto patient = ufc.getPatient(appointmentRequest.getPatientId()).getBody();
        assert patient != null;
        DoctorResponseDto doctor = ufc.getDoctorWithProfile(appointmentRequest.getDoctorId()).getBody();
        assert doctor != null;
        Boolean alreadyTaken = appointmentRepository.alreadyTaken(doctor.getId(),appointmentRequest.getAppointmentDate(), AppointmentStatus.PENDING);
        Boolean canceled = appointmentRepository.canceled(patient.getId(), AppointmentStatus.CANCELLED, AppointmentRequest.APPOINTMENT_BEFORE_DAYS, AppointmentRequest.APPOINTMENT_AFTER_DAYS);
        Boolean hasPendingAppointment = appointmentRepository.pending(patient.getId(), AppointmentStatus.PENDING);
        appointmentRequest.validate(doctor.getDoctorProfile(), alreadyTaken, hasPendingAppointment, canceled);
        AppointmentEntity appointment = new AppointmentEntity(patient.getId(), doctor.getId(), appointmentRequest.getAppointmentDate(), AppointmentStatus.PENDING, null);
        this.createAppointmentBill(appointment, patient, doctor);
        appointment = appointmentRepository.save(appointment);
        return mapAppointmentToDto(appointment);
    }

    @Override
    public AppointmentPatientResponseDto updateAppointment(String appointmentId, AppointmentRequest appointmentRequest) {
        return null;
    }

    @Override
    public String cancelAppointment(String appointmentId) {
        return "";
    }

    @Override
    public void createAppointmentBill(AppointmentEntity appointment, PatientResponseDto patient, DoctorResponseDto doctor) {
        try {
            pdfGenerationService.generateAndSavePdfAsync(appointment, patient, doctor);
        } catch(Exception e){
            throw new RuntimeException("Internal server error");
        }
    }

    @Override
    public Resource getMyAppointmentBill(String appointmentId) {
        return null;
    }

    @Override
    public Page<AppointmentDoctorResponseDto> getDoctorAppointments(String userId, Pageable pageable) {
        return null;
    }

    @Override
    public AppointmentDoctorResponseDto getDoctorAppointment(String appointmentId) {
        return null;
    }

    @Override
    public AppointmentDoctorResponseDto changeStatus(AppointmentStatus status, String appointmentId) {
        return null;
    }

    @Override
    public ReportResponseDto addReportToAppointment(ReportRequest reportCommand, String appointmentId) {
        return null;
    }

    @Override
    public ReportResponseDto editReportAppointment(ReportRequest reportCommand, String reportId) {
        return null;
    }


    // Private helper method to map AppointmentEntity to AppointmentPatientResponseDto
    private AppointmentPatientResponseDto mapAppointmentToDto(AppointmentEntity appointment) {
        AppointmentPatientResponseDto dto = new AppointmentPatientResponseDto();

        // Fetch the doctor details
        DoctorResponseDto doctor = ufc.getDoctorWithProfile(appointment.getDoctorId()).getBody();

        // Fetch the report related to the appointment (if it exists)
        ReportEntity report = reportRepository.findByAppointmentId(appointment.getId()).orElse(null);

        // Set the properties of the DTO
        dto.setId(appointment.getId());
        dto.setDoctor(doctor);
        dto.setAppointmentDate(appointment.getAppointmentDate());
        dto.setStatus(appointment.getStatus());
        dto.setBillUrl("http://localhost:8888/api/appointment/bill/" + appointment.getId());

        // Set the report details if available
        if (report != null) {
            ReportResponseDto reportDto = new ReportResponseDto();
            reportDto.setId(report.getId());
            reportDto.setDiagnosis(report.getDiagnosis());
            reportDto.setNotes(report.getNotes());
            reportDto.setTreatment(report.getTreatment());
            dto.setReport(reportDto);
        } else {
            dto.setReport(null);
        }

        return dto;
    }
}
