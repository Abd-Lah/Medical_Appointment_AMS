package org.medical.appointmentservice.controller;

import lombok.RequiredArgsConstructor;
import org.medical.appointmentservice.dto.request.AppointmentRequest;
import org.medical.appointmentservice.dto.request.AppointmentStatusRequest;
import org.medical.appointmentservice.dto.request.ReportRequest;
import org.medical.appointmentservice.dto.response.AppointmentDoctorResponseDto;
import org.medical.appointmentservice.dto.response.AppointmentPatientResponseDto;
import org.medical.appointmentservice.dto.response.ReportResponseDto;
import org.medical.appointmentservice.service.AppointmentService;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {
    private final AppointmentService appointmentService;

    @GetMapping(path = "/patient/{patientId}")
    public ResponseEntity<Page<AppointmentPatientResponseDto>> getPatientAppointments(@PathVariable("patientId") String patientId,
                                                                                     @RequestParam(required = false, defaultValue = "0") int page,
                                                                                     @RequestParam(required = false, defaultValue = "10") int size,
                                                                                     @RequestParam(required = false, defaultValue = "asc") String orderBy) {
        Pageable pageable = PageRequest.of(page, size);
        return new ResponseEntity<>(appointmentService.getPatientAppointments(patientId, orderBy, pageable), HttpStatus.OK);
    }

    @GetMapping(path = "/patient/{patientId}/{appointmentId}")
    public ResponseEntity<AppointmentPatientResponseDto> getPatientAppointment(@PathVariable("patientId") String patientId, @PathVariable("appointmentId") String appointmentId) {
        return new ResponseEntity<>(appointmentService.getPatientAppointment(appointmentId, patientId), HttpStatus.OK);
    }

    @GetMapping(path = "/doctor/{doctorId}")
    public ResponseEntity<Page<AppointmentDoctorResponseDto>> getDoctorAppointments(@PathVariable("doctorId") String doctorId,
                                                                                     @RequestParam(required = false, defaultValue = "0") int page,
                                                                                     @RequestParam(required = false, defaultValue = "10") int size,
                                                                                     @RequestParam(required = false, defaultValue = "asc") String orderBy) {
        Pageable pageable = PageRequest.of(page, size);
        return new ResponseEntity<>(appointmentService.getDoctorAppointments(doctorId, orderBy, pageable), HttpStatus.OK);
    }

    @GetMapping(path = "/doctor/{doctorId}/{appointmentId}")
    public ResponseEntity<AppointmentDoctorResponseDto> getDoctorAppointment(@PathVariable("doctorId") String doctorId, @PathVariable("appointmentId") String appointmentId) {
        return new ResponseEntity<>(appointmentService.getDoctorAppointment(appointmentId, doctorId), HttpStatus.OK);
    }

    @PostMapping(path = "/create")
    public ResponseEntity<AppointmentPatientResponseDto> createAppointment(@RequestBody AppointmentRequest appointmentRequest) {
        return new ResponseEntity<>(appointmentService.makeAppointment(appointmentRequest), HttpStatus.CREATED);
    }

    @PutMapping(path = "/update/{appointmentId}")
    public ResponseEntity<AppointmentPatientResponseDto> updateAppointment(@PathVariable("appointmentId") String appointmentId, @RequestBody AppointmentRequest appointmentRequest) {
        return new ResponseEntity<>(appointmentService.updateAppointment(appointmentId, appointmentRequest), HttpStatus.OK);
    }

    @DeleteMapping("/cancel/{appointmentId}/{patientId}")
    public ResponseEntity<String> cancelAppointment(@PathVariable("appointmentId") String appointmentId, @PathVariable("patientId") String patientId) {
        return new ResponseEntity<>(appointmentService.cancelAppointment(appointmentId, patientId), HttpStatus.OK);
    }

    @GetMapping("/billing_file/{appointmentId}/{patientId}")
    public ResponseEntity<Resource> appointmentBill(@PathVariable("appointmentId") String appointmentId, @PathVariable("patientId") String patientId) {

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)  // Set content type as PDF
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" +"Appointment"+ "\"")  // Display inline
                .body(appointmentService.getMyAppointmentBill(appointmentId, patientId));
    }

    @PutMapping("/doctor/change_status/{appointmentId}/{doctorId}")
    public ResponseEntity<AppointmentDoctorResponseDto> changeStatus(@PathVariable("appointmentId") String appointmentId, @PathVariable("doctorId") String doctorId, @RequestBody AppointmentStatusRequest appointmentStatusRequest) {
        return new ResponseEntity<>(appointmentService.changeStatus(doctorId,appointmentStatusRequest.validate(), appointmentId), HttpStatus.OK);
    }

    @PostMapping("/{appointmentId}/report/create")
    public ResponseEntity<ReportResponseDto> createReport(@PathVariable("appointmentId") String appointmentId, @RequestBody ReportRequest reportCommand) {
        return new ResponseEntity<>(appointmentService.addReportToAppointment(reportCommand, appointmentId), HttpStatus.CREATED);
    }

    @PutMapping("/{reportId}/report/update")
    public ResponseEntity<ReportResponseDto> updateReport(@PathVariable("reportId") String reportId, @RequestBody ReportRequest reportCommand) {
        return new ResponseEntity<>(appointmentService.editReportAppointment(reportCommand, reportId), HttpStatus.CREATED);
    }
}
