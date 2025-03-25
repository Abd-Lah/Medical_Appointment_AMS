package org.medical.doctorservice.feign;

import org.medical.doctorservice.dto.request.AppointmentStatusRequest;
import org.medical.doctorservice.dto.request.ReportRequest;
import org.medical.doctorservice.dto.response.AppointmentResponseDto;
import org.medical.doctorservice.dto.response.ReportResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "appointment-service", path = "/api/appointments")
public interface AppointmentFeignClient {
    @GetMapping(path = "/doctor/{doctorId}")
    ResponseEntity<Page<AppointmentResponseDto>> getDoctorAppointments(@PathVariable("doctorId") String doctorId,
                                                                       @RequestParam(required = false, defaultValue = "0") int page,
                                                                       @RequestParam(required = false, defaultValue = "10") int size,
                                                                       @RequestParam(required = false, defaultValue = "asc") String orderBy);

    @GetMapping(path = "/doctor/{doctorId}/{appointmentId}")
    ResponseEntity<AppointmentResponseDto> getDoctorAppointment(@PathVariable("doctorId") String doctorId, @PathVariable("appointmentId") String appointmentId);
    @PutMapping("/doctor/change_status/{appointmentId}/{doctorId}")
    ResponseEntity<AppointmentResponseDto> changeStatus(@PathVariable("appointmentId") String appointmentId, @PathVariable("doctorId") String doctorId, @RequestBody AppointmentStatusRequest appointmentStatusRequest);

    @PostMapping("/{appointmentId}/report/create")
    ResponseEntity<ReportResponseDto> createReport(@PathVariable("appointmentId") String appointmentId, @RequestBody ReportRequest reportCommand);
    @PutMapping("/{reportId}/report/update")
    ResponseEntity<ReportResponseDto> updateReport(@PathVariable("reportId") String reportId, @RequestBody ReportRequest reportCommand);
}
