package com.tfg.app.controller.DTOS;

public class ReportDTO {
    private String name;
    private String lastName;
    private String dni;
    private String address;
    private String phone;
    private String descriptionAppointment;
    private String reportIntervention;
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public String getDescriptionAppointment() {
        return descriptionAppointment;
    }
    public void setDescriptionAppointment(String descriptionAppointment) {
        this.descriptionAppointment = descriptionAppointment;
    }
    public String getReportIntervention() {
        return reportIntervention;
    }
    public void setReportIntervention(String reportIntervention) {
        this.reportIntervention = reportIntervention;
    }
    public String getDni() {
        return dni;
    }
    public void setDni(String dni) {
        this.dni = dni;
    }

}
