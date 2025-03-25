package org.medical.patientservice.feign;

import org.medical.patientservice.dto.request.AppointmentRequest;
import org.medical.patientservice.dto.response.AppointmentResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "appointment-service", path = "/api/appointments")
public interface AppointmentFeignClient {
    @GetMapping("/patient/{patientId}")
    ResponseEntity<Page<AppointmentResponseDto>> getPatientAppointments(
            @PathVariable("patientId") String patientId,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "asc") String orderBy);

    @GetMapping(path = "/patient/{patientId}/{appointmentId}")
    ResponseEntity<AppointmentResponseDto> getPatientAppointment(@PathVariable("patientId") String patientId, @PathVariable("appointmentId") String appointmentId);

    @PostMapping(path = "/create")
    ResponseEntity<AppointmentResponseDto> createAppointment(@RequestBody AppointmentRequest appointmentRequest);

    @PutMapping(path = "/update/{appointmentId}")
    ResponseEntity<AppointmentResponseDto> updateAppointment(@PathVariable("appointmentId") String appointmentId, @RequestBody AppointmentRequest appointmentRequest);

    @DeleteMapping("/cancel/{appointmentId}/{patientId}")
    ResponseEntity<String> cancelAppointment(@PathVariable("appointmentId") String appointmentId, @PathVariable("patientId") String patientId);

    @GetMapping("/billing_file/{appointmentId}/{patientId}")
    ResponseEntity<Resource> appointmentBill(@PathVariable("appointmentId") String appointmentId, @PathVariable("patientId") String patientId);

}
