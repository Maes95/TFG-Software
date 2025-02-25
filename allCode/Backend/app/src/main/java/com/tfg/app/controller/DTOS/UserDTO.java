package com.tfg.app.controller.DTOS;

import java.time.LocalDate;

import com.tfg.app.model.User;

public class UserDTO {
    
    private String name;
    private String lastName;
    private String username;
    private String email;
    private String passwordEncoded;
    private String address;
    private String city;
    private String country;
    private String postalCode;
    private String phone;
    private String gender;
    private LocalDate birth;
    private User doctorAsignated;
    private Long codEntity;

   

    public UserDTO(String name, String lastName, String username, String email, String passwordEncoded, String address,
            String city, String country, String postalCode, String phone, String gender, LocalDate birth,
            User doctorAsignated, Long codEntity) {
        this.name = name;
        this.lastName = lastName;
        this.username = username;
        this.email = email;
        this.passwordEncoded = passwordEncoded;
        this.address = address;
        this.city = city;
        this.country = country;
        this.postalCode = postalCode;
        this.phone = phone;
        this.gender = gender;
        this.birth = birth;
        this.doctorAsignated = doctorAsignated;
        this.codEntity = codEntity;
    }

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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    

    public void setPasswordEncoded(String passwordEncoded) {
        this.passwordEncoded = passwordEncoded;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public LocalDate getBirth() {
        return birth;
    }

    public void setBirth(LocalDate birth) {
        this.birth = birth;
    }

    public String getPasswordEncoded() {
        return passwordEncoded;
    }

    public User getDoctorAsignated() {
        return doctorAsignated;
    }

    public void setDoctorAsignated(User doctorAsignated) {
        this.doctorAsignated = doctorAsignated;
    }

    public Long getCodEntity() {
        return codEntity;
    }

    public void setCodEntity(Long codEntity) {
        this.codEntity = codEntity;
    }

    
}
