package com.tfg.app.controller.rest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.lowagie.text.DocumentException;
import com.tfg.app.controller.DTOS.ReportDTO;
import com.tfg.app.model.Appointment;
import com.tfg.app.model.Intervention;
import com.tfg.app.model.User;
import com.tfg.app.model.Util;
import com.tfg.app.service.AppointmentService;
import com.tfg.app.service.InterventionService;
import com.tfg.app.service.UserService;
import com.tfg.app.service.UtilService;
import com.tfg.app.util.ExporterExcel;
import com.tfg.app.util.ExporterPDF;

@RestController
@RequestMapping("/api/util")
public class UtilRestController {

    @Autowired
    private UserService userService;
    @Autowired
    private AppointmentService appointmentService;
    @Autowired
    private InterventionService interventionService;
    @Autowired
    private UtilService utilService;

    @GetMapping("/exportPatientsPDF/{id}")
    public void exportPatientListPDF(HttpServletResponse response, @PathVariable Long id) throws DocumentException, IOException {
        response.setContentType("application/pdf");

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormat.format(new Date());

        String header = "Content-Disposition";
        String value = "attachment; filename=Pacientes_" + currentDateTime + ".pdf";

        response.setHeader(header, value);

        try{
            List<User> patients = userService.findAllUsersByDoctorAsignatedId(id);
            ExporterPDF exporter = new ExporterPDF();
            exporter.setPatientList(patients);
            exporter.exportPatients(response);
        } catch (Exception e) {
            e.printStackTrace();;
        }
    }
  

    @GetMapping("/exportAppointmentsPDF")
    public void exportAppointmentListPDF(HttpServletResponse repsonse) throws DocumentException, IOException {
        repsonse.setContentType("application/pdf");

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormat.format(new Date());

        String header = "Content-Disposition";
        String value = "attachment; filename=Citas_" + currentDateTime + ".pdf";

        repsonse.setHeader(header, value);

        List<Appointment> appointments = appointmentService.findAll();

        ExporterPDF exporter = new ExporterPDF();
        exporter.setAppointmentList(appointments);
        exporter.exportAppointments(repsonse);

    }

    @GetMapping("/exportInterventionsPDF/{id}")
    public void exportInterventionsPDF(HttpServletResponse repsonse, @PathVariable Long id)
            throws DocumentException, IOException {
        repsonse.setContentType("application/pdf");

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormat.format(new Date());

        String header = "Content-Disposition";
        String value = "attachment; filename=Intervenciones_" + currentDateTime + ".pdf";

        repsonse.setHeader(header, value);

        List<Intervention> interventions = interventionService.findByUserId(id);

        ExporterPDF exporter = new ExporterPDF();
        exporter.setInterventionList(interventions);
        exporter.exportIntervention(repsonse);

    }

    /////////////////////////////////////////////////////////////

    @GetMapping("/exportPatientsExcel/{id}")
    public void exportPatientListExcel(HttpServletResponse response, @PathVariable Long id) throws DocumentException, IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormat.format(new Date());

        String header = "Content-Disposition";
        String value = "attachment; filename=Pacientes_" + currentDateTime + ".xlsx";

        response.setHeader(header, value);

        List<User> patients = userService.findAllUsersByDoctorAsignatedId(id);

        ExporterExcel exporter = new ExporterExcel(patients);

        exporter.exportPatients(response);
    }

    ////////////////////////////////////////////////////////////////////////////////////////

    @GetMapping("/aptComplYest")
    public int appointmentsCompletedYesterday() {
        return utilService.getAppointmentsCompletedYesterday();
    }

    @GetMapping("/numPatientsYesterday")
    public int numPatientsYesterday() {
        return utilService.getNumPatientsYesterday();
    }

    @GetMapping("/numPatientsTotal")
    public int numPatientsTotal() {
        return utilService.getNumPatientsTotal();
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateUtil(@RequestParam(value = "appointmentsCompletedYesterday") int aptComplYest,
            @RequestParam(value = "numPatientsYesterday") int numPatientsYesterday,
            @RequestParam(value = "numPatientsTotal") int numPatientsTotal) {

        Util utiltoUpdt = new Util();

        if (aptComplYest != 0) {
            utiltoUpdt.setAppointmentsCompletedYesterday(aptComplYest);
        }
        if (numPatientsTotal != 0) {
            utiltoUpdt.setNumPatientsTotal(numPatientsTotal);
        }
        if (numPatientsYesterday != 0) {
            utiltoUpdt.setNumPatientsYesterday(numPatientsYesterday);
        }
        Util updatedUtil = utilService.partialUpdate(23L, utiltoUpdt);

        return ResponseEntity.ok(updatedUtil);
    }

    /////////////////////////////////////////

    @PostMapping("/generatePDF")
    public ResponseEntity<byte[]> generatePdf(@RequestBody ReportDTO report) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            // Crea un nuevo documento PDF
            PdfWriter writer = new PdfWriter(byteArrayOutputStream);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Suponiendo que tienes una imagen para agregar al documento
            Resource resource = new ClassPathResource("static/documents/SmileLink.png");
            Image image = new Image(ImageDataFactory.create(resource.getURL()));
            document.add(image);

            // Añade contenido al documento
            document.add(new Paragraph("Paciente: " + report.getName() + " " + report.getLastName()));
            document.add(new Paragraph("DNI: " + report.getDni()));
            document.add(new Paragraph("Dirección: " + report.getAddress()));
            document.add(new Paragraph("Teléfono: " + report.getPhone()));
            document.add(new Paragraph("Fecha: " + LocalDate.now().toString()));
            document.add(new Paragraph(""));
            document.add(new Paragraph("Motivo de la cita: " + report.getDescriptionAppointment()));
            document.add(new Paragraph(""));
            document.add(new Paragraph("Conclusiones médicas:"));
            document.add(new Paragraph(report.getReportIntervention()));

            // Cierra el documento
            document.close();

            byte[] pdfContents = byteArrayOutputStream.toByteArray();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            // Aquí se configura para descargar el archivo como attachment
            String filename = "filename.pdf";
            headers.setContentDispositionFormData(filename, filename);
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
            return new ResponseEntity<>(pdfContents, headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}