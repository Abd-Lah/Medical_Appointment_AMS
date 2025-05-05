package org.medical.doctorservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.medical.doctorservice.dto.request.AppointmentStatusRequest;
import org.medical.doctorservice.dto.request.DoctorProfileRequest;
import org.medical.doctorservice.dto.request.ReportRequest;
import org.medical.doctorservice.dto.response.AppointmentResponseDto;
import org.medical.doctorservice.dto.response.ReportResponseDto;
import org.medical.doctorservice.exception.InvalidRequestException;
import org.medical.doctorservice.feign.AppointmentFeignClient;
import org.medical.doctorservice.feign.UserFeignClient;
import org.medical.doctorservice.model.DoctorProfileEntity;
import org.medical.doctorservice.repository.DoctorProfileRepository;
import org.medical.doctorservice.service.DoctorService;
import org.medical.doctorservice.util.FeignResponseHelper;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DoctorServiceImp implements DoctorService {
    private final DoctorProfileRepository drp;
    private final UserFeignClient userFeignClient;
    private final AppointmentFeignClient afc;
    private final FeignResponseHelper helper;

    @Override
    public DoctorProfileEntity getProfile(String id) {
        return drp.getDoctorProfileByDoctorId(id);
    }

    @Override
    @Transactional
    public DoctorProfileEntity createProfile(DoctorProfileRequest request) {
        // checking if really doctor;
        DoctorProfileEntity profile = request.createProfile();
        return drp.save(profile);
    }

    @Override
    @Transactional
    public DoctorProfileEntity updateProfile(String id, DoctorProfileRequest request) {
        helper.checkResponseStatus(userFeignClient.getDoctor(request.getDoctorId()));
        DoctorProfileEntity profile = drp.getDoctorProfileByDoctorId(id);
        if(profile.getDoctorId().equals(request.getDoctorId())){
            profile.updateDoctorProfile(request);
            return drp.save(profile);
        }else{
            throw new InvalidRequestException("Invalid request");
        }
    }

    @Override
    @Transactional
    public void deleteProfile(String id) {
        DoctorProfileEntity profile = drp.getDoctorProfileByDoctorId(id);
        profile.setDeleted(true);
        drp.save(profile);
    }

    @Override
    public void activateProfile(String id) {
        DoctorProfileEntity profile = drp.getDoctorProfileByDoctorId(id);
        profile.setDeleted(false);
        drp.save(profile);
    }

    @Override
    public ResponseEntity<Page<AppointmentResponseDto>> getDoctorAppointments(String doctorId, int page, int size, String orderBy) {
        return afc.getDoctorAppointments(doctorId, page, size, orderBy);
    }

    @Override
    public ResponseEntity<AppointmentResponseDto> getDoctorAppointment(String doctorId, String appointmentId) {
        return afc.getDoctorAppointment(doctorId, appointmentId);
    }

    @Override
    public ResponseEntity<AppointmentResponseDto> changeStatus(String appointmentId, String doctorId, AppointmentStatusRequest appointmentStatusRequest) {
        appointmentStatusRequest.validate();
        return afc.changeStatus(appointmentId, doctorId, appointmentStatusRequest);
    }

    @Override
    public ResponseEntity<ReportResponseDto> createReport(String appointmentId, ReportRequest reportCommand) {
        return afc.createReport(appointmentId, reportCommand);
    }

    @Override
    public ResponseEntity<ReportResponseDto> updateReport(String reportId, ReportRequest reportCommand) {
        return afc.updateReport(reportId, reportCommand);
    }
}
