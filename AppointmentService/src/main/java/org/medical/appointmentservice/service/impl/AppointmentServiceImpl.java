package org.medical.appointmentservice.service.impl;


import lombok.RequiredArgsConstructor;
import org.medical.appointmentservice.dto.request.AppointmentRequest;
import org.medical.appointmentservice.dto.request.ReportRequest;
import org.medical.appointmentservice.dto.response.*;
import org.medical.appointmentservice.exception.InvalidRequestException;
import org.medical.appointmentservice.exception.ResourceNotFoundException;
import org.medical.appointmentservice.exception.ValidationException;
import org.medical.appointmentservice.feign.UserFeignClient;
import org.medical.appointmentservice.model.AppointmentEntity;
import org.medical.appointmentservice.model.AppointmentStatus;
import org.medical.appointmentservice.model.ReportEntity;
import org.medical.appointmentservice.repository.AppointmentRepository;
import org.medical.appointmentservice.repository.ReportRepository;
import org.medical.appointmentservice.service.AppointmentService;
import org.medical.appointmentservice.util.Helper;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final ReportRepository reportRepository;
    private final UserFeignClient ufc;
    private final PdfGenerationService pdfGenerationService;
    private final Helper<AppointmentEntity> helperAppointment;
    private final Helper<ReportEntity> helperReport;

    @Override
    public Page<AppointmentPatientResponseDto> getPatientAppointments(String userId, String orderBy, Pageable pageable) {
        PatientResponseDto patient = ufc.getPatient(userId).getBody();
        assert patient != null;
        Page<AppointmentEntity> myAppointments = appointmentRepository.getAppointments(patient.getId(), orderBy, pageable);
        return myAppointments.map(this::mapAppointmentPatientToDto);
    }



    @Override
    public AppointmentPatientResponseDto getPatientAppointment(String appointmentId, String patientId) {
        AppointmentEntity appointment = appointmentRepository.findByAppointmentIdAndByUserId(appointmentId, patientId);
        helperAppointment.isObjectNull(appointment, "Appointment with id " + appointmentId + " not found.");
        assert appointment != null;
        return mapAppointmentPatientToDto(appointment);
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
        this.appointmentBill(appointment, patient, doctor);
        appointment = appointmentRepository.save(appointment);
        return mapAppointmentPatientToDto(appointment);
    }

    @Override
    @Transactional
    public AppointmentPatientResponseDto updateAppointment(String appointmentId, AppointmentRequest appointmentRequest) {
        PatientResponseDto patient = ufc.getPatient(appointmentRequest.getPatientId()).getBody();
        assert patient != null;
        AppointmentEntity existingAppointment = appointmentRepository.findByAppointmentIdAndByUserId(appointmentId,patient.getId());
        helperAppointment.isObjectNull(existingAppointment, "No appointment found");
        DoctorResponseDto doctor = ufc.getDoctorWithProfile(existingAppointment.getDoctorId()).getBody();
        assert doctor != null;
        Boolean alreadyTaken = appointmentRepository.alreadyTaken(doctor.getId(),appointmentRequest.getAppointmentDate(), AppointmentStatus.PENDING);
        appointmentRequest.validateInUpdate(existingAppointment,doctor.getDoctorProfile(),alreadyTaken, false);
        existingAppointment.setAppointmentDate(appointmentRequest.getAppointmentDate());
        this.appointmentBill(existingAppointment, patient, doctor);
        appointmentRepository.save(existingAppointment);
        return mapAppointmentPatientToDto(existingAppointment);
    }

    @Override
    @Transactional
    public String cancelAppointment(String appointmentId, String patientId) {
        PatientResponseDto patient = ufc.getPatient(patientId).getBody();
        assert patient != null;
        AppointmentEntity existingAppointment = appointmentRepository.findByAppointmentIdAndByUserId(appointmentId,patient.getId());
        helperAppointment.isObjectNull(existingAppointment, "No appointment found");
        DoctorResponseDto doctor = ufc.getDoctorWithProfile(existingAppointment.getDoctorId()).getBody();
        assert doctor != null;
        if(!existingAppointment.getAppointmentDate().isAfter(LocalDateTime.now().plusHours(AppointmentRequest.APPOINTMENT_BEFORE_HOURS))) {
            throw new InvalidRequestException("You cant canceler this appointment, because it still less than 8 hours");
        }
        existingAppointment.setStatus(AppointmentStatus.CANCELLED);
        appointmentRepository.save(existingAppointment);
        this.appointmentBill(existingAppointment, patient, doctor);
        return "Appointment cancelled";
    }

    @Override
    public void appointmentBill(AppointmentEntity appointment, PatientResponseDto patient, DoctorResponseDto doctor) {
        try {
            pdfGenerationService.generateAndSavePdfAsync(appointment, patient, doctor);
        } catch(Exception e){
            throw new RuntimeException("Internal server error");
        }
    }

    @Override
    public Resource getMyAppointmentBill(String appointmentId, String patientId) {
        PatientResponseDto patient = ufc.getPatient(patientId).getBody();
        assert patient != null;
        AppointmentEntity existingAppointment = appointmentRepository.findByAppointmentIdAndByUserId(appointmentId,patient.getId());
        helperAppointment.isObjectNull(existingAppointment, "No appointment found");
        if(existingAppointment.getAppointmentDate().isBefore(LocalDateTime.now()) || existingAppointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new InvalidRequestException("Billing appointment file no longer exists");
        }
        return this.GetMyAppointmentBill(appointmentId+".pdf");
    }

    @Override
    public Page<AppointmentDoctorResponseDto> getDoctorAppointments(String userId,String orderBy, Pageable pageable) {
        DoctorResponseDto doctor = ufc.getDoctorWithProfile(userId).getBody();
        assert doctor != null;
        Page<AppointmentEntity> myAppointments = appointmentRepository.getAppointments(doctor.getId(), orderBy, pageable);
        return myAppointments.map(this::mapAppointmentDoctorToDto);
    }

    @Override
    public AppointmentDoctorResponseDto getDoctorAppointment(String appointmentId, String doctorId) {
        AppointmentEntity appointment = appointmentRepository.findByAppointmentIdAndByUserId(appointmentId, doctorId);
        helperAppointment.isObjectNull(appointment, "Appointment with id " + appointmentId + " not found.");
        assert appointment != null;
        return mapAppointmentDoctorToDto(appointment);
    }

    @Override
    @Transactional
    public AppointmentDoctorResponseDto changeStatus(String doctorId, AppointmentStatus status, String appointmentId) {
        DoctorResponseDto doctor = ufc.getDoctorWithProfile(doctorId).getBody();
        assert doctor != null;

        AppointmentEntity appointment = appointmentRepository.findByAppointmentIdAndByUserId(appointmentId, doctor.getId());
        helperAppointment.isObjectNull(appointment,"No appointment found");
        if(appointment.getAppointmentDate().isAfter(LocalDateTime.now())) {
            if(status == AppointmentStatus.APPROVED) {
                throw new ValidationException("Cannot change status of appointment");
            }
        }
        appointment.setStatus(status);
        PatientResponseDto patient = ufc.getPatient(appointment.getPatientId()).getBody();
        assert patient != null;
        this.appointmentBill(appointment, patient, doctor);
        appointmentRepository.save(appointment);
        return mapAppointmentDoctorToDto(appointment);
    }

    @Override
    public ReportResponseDto addReportToAppointment(ReportRequest reportCommand, String appointmentId) {
        DoctorResponseDto doctor = ufc.getDoctorWithProfile(reportCommand.getDoctorId()).getBody();
        assert doctor != null;

        AppointmentEntity appointment = appointmentRepository.findByAppointmentIdAndByUserId(appointmentId, doctor.getId());
        helperAppointment.isObjectNull(appointment,"No appointment found");
        if(!(appointment.getStatus() == AppointmentStatus.APPROVED)) {
            throw new ValidationException("The appointment is "+appointment.getStatus().toString().toLowerCase()+". To add report should be approved !");
        }
        ReportEntity report = reportRepository.save(new ReportEntity(reportCommand, appointment));
        return toReportDto(report);
    }

    @Override
    @Transactional
    public ReportResponseDto editReportAppointment(ReportRequest reportCommand, String reportId) {
        DoctorResponseDto doctor = ufc.getDoctorWithProfile(reportCommand.getDoctorId()).getBody();
        assert doctor != null;
        ReportEntity existingReport = reportRepository.findById(reportId).orElse(null);
        helperReport.isObjectNull(existingReport,"No report found");
        assert existingReport != null;
        AppointmentEntity appointment = appointmentRepository.findByAppointmentIdAndByUserId(existingReport.getAppointment().getId(), doctor.getId());
        helperAppointment.isObjectNull(appointment,"No appointment found");
        existingReport.update(reportCommand);
        reportRepository.save(existingReport);
        return toReportDto(existingReport);
    }


    // Private helper method to map AppointmentEntity to AppointmentPatientResponseDto
    private AppointmentPatientResponseDto mapAppointmentPatientToDto(AppointmentEntity appointment) {
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
        dto.setBillUrl("http://localhost:8888/patient-service/api/patient/"+appointment.getPatientId()+"/appointment/"+appointment.getId()+"/billing_file");

        dto.setReport(this.toReportDto(report));

        return dto;
    }

    private AppointmentDoctorResponseDto mapAppointmentDoctorToDto(AppointmentEntity appointment) {
        AppointmentDoctorResponseDto dto = new AppointmentDoctorResponseDto();

        // Fetch the doctor details
        PatientResponseDto patient = ufc.getPatient(appointment.getPatientId()).getBody();

        // Fetch the report related to the appointment (if it exists)
        ReportEntity report = reportRepository.findByAppointmentId(appointment.getId()).orElse(null);

        // Set the properties of the DTO
        dto.setId(appointment.getId());
        dto.setPatient(patient);
        dto.setAppointmentDate(appointment.getAppointmentDate());
        dto.setStatus(appointment.getStatus());

        // Set the report details if available
        dto.setReport(this.toReportDto(report));

        return dto;
    }
    private ReportResponseDto toReportDto(ReportEntity report) {
        if (report != null) {
            ReportResponseDto reportDto = new ReportResponseDto();
            reportDto.setId(report.getId());
            reportDto.setDiagnosis(report.getDiagnosis());
            reportDto.setNotes(report.getNotes());
            reportDto.setTreatment(report.getTreatment());
            return reportDto;
        } else {
           return null;
        }
    }

    private Resource GetMyAppointmentBill(String filename) {
        String FILE_STORAGE_PATH = "/opt/appointment_medical_app/appointment_bills/";
        Path filePath = Paths.get(FILE_STORAGE_PATH).resolve(filename);  // Construct the full file path

        File file = filePath.toFile();

        if (!file.exists()) {
            throw new ResourceNotFoundException("File not found");
        }

        Resource resource = new FileSystemResource(file);

        // Check if resource exists
        if (!resource.exists()) {
            throw new ResourceNotFoundException("File not found");
        }

        return resource;

    }
}
