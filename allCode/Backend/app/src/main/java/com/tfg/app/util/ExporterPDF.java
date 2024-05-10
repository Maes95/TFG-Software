package com.tfg.app.util;

import java.awt.Color;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.lowagie.text.Anchor;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.tfg.app.model.Appointment;
import com.tfg.app.model.Intervention;
import com.tfg.app.model.User;

public class ExporterPDF {

    private List<User> patientList;
    private List<Appointment> appointmentList;
    private List<Intervention> interventionList;

    public ExporterPDF() {
    }

    public void setInterventionList(List<Intervention> interventionList) {
        this.interventionList = interventionList;
    }

    public void setPatientList(List<User> patientList) {
        this.patientList = patientList;
    }

    public void setAppointmentList(List<Appointment> appointmentList) {
        this.appointmentList = appointmentList;
    }

    // Export PDF Patients
    private void headerPatientsTable(PdfPTable table) {
        PdfPCell cell = new PdfPCell();

        cell.setBackgroundColor(Color.blue);
        cell.setPadding(5);

        Font font = FontFactory.getFont(FontFactory.HELVETICA);
        font.setColor(Color.WHITE);

        cell.setPhrase(new Phrase("Nombre", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Apellidos", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("DNI", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Teléfono", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Correo electrónico", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Fecha de nacimiento", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Género", font));
        table.addCell(cell);
    }

    private void writePatientsData(PdfPTable table) {
        for (User user : patientList) {
            table.addCell(user.getName());
            table.addCell(user.getLastName());
            table.addCell(user.getUsername());
            table.addCell(user.getPhone());
            table.addCell(user.getEmail());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            table.addCell(user.getBirth().format(formatter).toString());
            table.addCell(user.getGender());
        }
    }

    public void exportPatients(HttpServletResponse response) throws DocumentException, IOException {
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();

        Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        font.setColor(Color.BLACK);
        font.setSize(14);

        Paragraph title = new Paragraph("Lista de pacientes", font);
        title.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(title);

        PdfPTable table = new PdfPTable(7);
        table.setWidthPercentage(100);
        table.setSpacingBefore(15);
        table.setWidths(new float[] { 2f, 2f, 2.1f, 2f, 4.5f, 2f, 2f });
        table.setWidthPercentage(110);

        headerPatientsTable(table);
        writePatientsData(table);

        document.add(table);
        document.close();
    }

    //////////////////////////////////////////////////////////////////////////////////
    // Export PDF Appointments
    private void headerAppointmentsTable(PdfPTable table) {
        PdfPCell cell = new PdfPCell();

        cell.setBackgroundColor(Color.blue);
        cell.setPadding(5);

        Font font = FontFactory.getFont(FontFactory.HELVETICA);
        font.setColor(Color.WHITE);

        cell.setPhrase(new Phrase("Nombre", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Apellidos", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("DNI", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Fecha de nacimiento", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Motivo", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Hora de inicio", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Hora de fin", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Teléfono", font));
        table.addCell(cell);
    }

    private void writeAppointmentsData(PdfPTable table) {
        for (Appointment appointment : appointmentList) {
            if (!appointment.isCompleted()) {
                table.addCell(appointment.getUser().getName());
                table.addCell(appointment.getUser().getLastName());
                table.addCell(appointment.getUser().getUsername());
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                table.addCell(appointment.getUser().getBirth().format(formatter).toString());
                table.addCell(appointment.getDescription());
                DateTimeFormatter formatterH = DateTimeFormatter.ofPattern("HH:mm");
                table.addCell(appointment.getFromDate().format(formatterH).toString());
                table.addCell(appointment.getToDate().format(formatterH).toString());
                table.addCell(appointment.getUser().getPhone());
            }
        }
    }

    public void exportAppointments(HttpServletResponse response) throws DocumentException, IOException {
        Document document = new Document(PageSize.A4.rotate());
        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();

        Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        font.setColor(Color.BLACK);
        font.setSize(14);

        Paragraph title = new Paragraph("Citas pendientes", font);
        title.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(title);

        PdfPTable table = new PdfPTable(8);
        table.setWidthPercentage(90);
        table.setSpacingBefore(25);
        table.setWidths(new float[] { 2f, 2f, 2.1f, 2f, 4.5f, 2f, 2f, 2f });
        table.setWidthPercentage(100);

        headerAppointmentsTable(table);
        writeAppointmentsData(table);

        document.add(table);
        document.close();
    }

    ///////////////////////////////////////////////////////////////////////
    // Export PDF Interventions of Patient

    private void headerInterventionTable(PdfPTable table) {
        PdfPCell cell = new PdfPCell();

        cell.setBackgroundColor(Color.blue);
        cell.setPadding(5);

        Font font = FontFactory.getFont(FontFactory.HELVETICA);
        font.setColor(Color.WHITE);

        cell.setPhrase(new Phrase("Nombre", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Apellidos", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Fecha de nacimiento", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("DNI", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Fecha de intervención", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Tipo de intervención", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Documento", font));
        table.addCell(cell);

    }

    private void writeInterventionData(PdfPTable table) {

        for (Intervention intervention : interventionList) {
            table.addCell(intervention.getUser().getName());
            table.addCell(intervention.getUser().getLastName());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            table.addCell(intervention.getUser().getBirth().format(formatter).toString());
            table.addCell(intervention.getUser().getUsername());
            table.addCell(intervention.getInterventionDate().format(formatter).toString());
            table.addCell(intervention.getType());
            Anchor enlace = new Anchor(intervention.getDocument().getFileName(), createFontWithLink());
            enlace.setReference("https://www.google.es");
            // table.addCell(intervention.getDocument().getLink());
            table.addCell(enlace);
        }
    }

    private static Font createFontWithLink() {
        Font font = new Font(Font.HELVETICA);
        font.setColor(Color.BLUE);
        font.setSize(11);
        font.setStyle(Font.UNDERLINE);
        return font;
    }

    public void exportIntervention(HttpServletResponse response) throws DocumentException, IOException {
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();

        Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        font.setColor(Color.BLACK);
        font.setSize(14);

        Paragraph title = new Paragraph("Intervenciones", font);
        title.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(title);

        PdfPTable table = new PdfPTable(7);
        table.setWidthPercentage(100);
        table.setSpacingBefore(25);
        table.setWidths(new float[] { 2f, 2f, 2.2f, 2f, 2.3f, 3.4f, 2.2f });
        table.setWidthPercentage(110);

        headerInterventionTable(table);
        writeInterventionData(table);

        document.add(table);
        document.close();
    }
}
