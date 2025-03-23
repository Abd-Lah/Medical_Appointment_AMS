package org.medical.appointmentservice.service.impl;

import com.itextpdf.html2pdf.HtmlConverter;

import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.RequiredArgsConstructor;
import org.medical.appointmentservice.dto.response.DoctorResponseDto;
import org.medical.appointmentservice.dto.response.PatientResponseDto;
import org.medical.appointmentservice.model.AppointmentEntity;
import org.medical.appointmentservice.model.AppointmentStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class PdfGenerationService {

    private final Configuration freeMarkerConfig;
    private final EmailService emailService;

    private final String pdfStoragePath = "/opt/appointment_medical_app/appointment_bills/";

    @Async
    public CompletableFuture<Void> generateAndSavePdfAsync(AppointmentEntity appointment, PatientResponseDto patient, DoctorResponseDto doctor) throws IOException {

        // Check if the appointment is cancelled. If cancelled, remove the existing file (if it exists)
        if (!checkIfAppointmentCancelled(appointment, patient, doctor)) {
            // Generate HTML content from FreeMarker template
            String htmlContent = getHtmlContent(appointment, patient, doctor);
            // Generate the file path
            File bill = getFile(appointment.getId());

            // Convert HTML to PDF using iText
            convertToPdf(bill, htmlContent);

            //Send Notification
            //if(userService.getCurrentUser().getRole() == Role.PATIENT){
            emailService.sendEmail(patient.getEmail(), "Your Appointment", htmlContent);
            //}
        }
        return CompletableFuture.completedFuture(null);  // Indicate completion
    }


    private boolean checkIfAppointmentCancelled(AppointmentEntity appointment, PatientResponseDto patient, DoctorResponseDto doctor) {
        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            File existingPdf = new File(pdfStoragePath, appointment.getId() + ".pdf");
            if (existingPdf.exists()) existingPdf.delete();
            if (appointment.getAppointmentDate().isAfter(LocalDateTime.now())) {
                emailService.sendEmail(
                        patient.getEmail(),
                        "Appointment Cancelled",
                        "Your Appointment has been canceled by <strong>Dr " + doctor.getFirstName() + " " + doctor.getLastName() + "</strong>"
                );
            }
            return true;
        }
        return false;
    }


    private String getHtmlContent(AppointmentEntity appointment, PatientResponseDto patient, DoctorResponseDto doctor) {
        // Prepare data for the FreeMarker template
        Map<String, Object> model = new HashMap<>();
        // Add necessary data to the model
        model.put("appointment", appointment);
        model.put("doctor", doctor);
        model.put("doctorProfile", doctor.getDoctorProfile());
        model.put("patient", patient); // Ensure patient is mapped to PatientDto
        // Get the FreeMarker template
        try {
            Template template = freeMarkerConfig.getTemplate("appointmentBilling.ftl");
            return FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
        } catch (Exception e) {
            throw new RuntimeException("Internal server error");
        }
    }

    private File getFile(String appointmentId) {
        File file = new File(pdfStoragePath, appointmentId + ".pdf");

        // If the file exists (it was already generated before), delete it before generating a new one
        if (file.exists()) {
            file.delete();  // Delete the existing file
        }
        return file;
    }

    private void convertToPdf(File bill, String htmlContent) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try (FileOutputStream fileOutputStream = new FileOutputStream(bill)) {
            HtmlConverter.convertToPdf(htmlContent, outputStream);
            fileOutputStream.write(outputStream.toByteArray());
        }
    }
}