package org.medical.doctorservice.controller;

import lombok.RequiredArgsConstructor;
import org.medical.doctorservice.dto.mapper.DoctorProfileMapper;
import org.medical.doctorservice.dto.request.AppointmentStatusRequest;
import org.medical.doctorservice.dto.request.DoctorProfileRequest;
import org.medical.doctorservice.dto.request.ReportRequest;
import org.medical.doctorservice.dto.response.AppointmentResponseDto;
import org.medical.doctorservice.dto.response.DoctorProfileDtoResponse;
import org.medical.doctorservice.dto.response.ReportResponseDto;
import org.medical.doctorservice.model.DoctorProfileEntity;
import org.medical.doctorservice.service.DoctorService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/doctors")
@RequiredArgsConstructor
public class DoctorController {
    private final DoctorService doctorService;

    @GetMapping(path = "/{id}")
    public DoctorProfileDtoResponse getDoctorProfile(@PathVariable String id) {
        DoctorProfileEntity profile = doctorService.getProfile(id);
        return DoctorProfileMapper.INSTANCE.toDoctorProfileDto(profile);
    }

    @PostMapping(path = "/profile/create")
    public DoctorProfileDtoResponse createDoctorProfile(@RequestBody DoctorProfileRequest request){
        return DoctorProfileMapper.INSTANCE.toDoctorProfileDto(doctorService.createProfile(request));
    }

    @PutMapping(path = "/profile/update/{id}")
    public DoctorProfileDtoResponse updateDoctorProfile(@PathVariable String id, @RequestBody DoctorProfileRequest request){
        return DoctorProfileMapper.INSTANCE.toDoctorProfileDto(doctorService.updateProfile(id,request));
    }

    @DeleteMapping(path = "/profile/delete/{id}")
    public ResponseEntity<Void> deleteDoctorProfile(@PathVariable String id) {
        doctorService.deleteProfile(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping(path = "/profile/activate/{id}")
    public ResponseEntity<Void> activateDoctorProfile(@PathVariable String id) {
        doctorService.activateProfile(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(path = "/appointments/{doctorId}")
    public ResponseEntity<Page<AppointmentResponseDto>> getDoctorAppointments(@PathVariable("doctorId") String doctorId,
                                                                       @RequestParam(required = false, defaultValue = "0") int page,
                                                                       @RequestParam(required = false, defaultValue = "10") int size,
                                                                       @RequestParam(required = false, defaultValue = "asc") String orderBy){
        return doctorService.getDoctorAppointments(doctorId, page, size, orderBy);
    }

    @GetMapping(path = "/{doctorId}/appointment/{appointmentId}")
    public ResponseEntity<AppointmentResponseDto> getDoctorAppointment(@PathVariable("doctorId") String doctorId, @PathVariable("appointmentId") String appointmentId){
        return doctorService.getDoctorAppointment(doctorId, appointmentId);
    }

    @PutMapping("/{appointmentId}/appointment/{doctorId}/change_status")
    public  ResponseEntity<AppointmentResponseDto> changeStatus(@PathVariable("appointmentId") String appointmentId, @PathVariable("doctorId") String doctorId, @RequestBody AppointmentStatusRequest appointmentStatusRequest){
        return doctorService.changeStatus(appointmentId, doctorId, appointmentStatusRequest);
    }

    @PostMapping("/appointment/{appointmentId}/report/create")
    public ResponseEntity<ReportResponseDto> createReport(@PathVariable("appointmentId") String appointmentId, @RequestBody ReportRequest reportCommand){
        return doctorService.createReport(appointmentId, reportCommand);
    }
    @PutMapping("/appointment/{reportId}/report/update")
    public ResponseEntity<ReportResponseDto> updateReport(@PathVariable("reportId") String reportId, @RequestBody ReportRequest reportCommand){
        return doctorService.updateReport(reportId, reportCommand);
    }
}
