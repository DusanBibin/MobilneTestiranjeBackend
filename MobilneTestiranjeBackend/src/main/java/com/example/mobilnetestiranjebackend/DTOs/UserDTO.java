package com.example.mobilnetestiranjebackend.DTOs;

import com.example.mobilnetestiranjebackend.enums.Role;
import com.example.mobilnetestiranjebackend.model.User;
import com.example.mobilnetestiranjebackend.model.Verification;
import jakarta.persistence.*;

public class UserDTO {
    private String firstName;
    private String lastname;
    private String email;
    private String phoneNumber;
    private String address;

    public UserDTO(User user) {
        this.firstName = user.getFirstName();
        this.lastname = user.getLastname();
        this.email = user.getEmail();
        this.phoneNumber = user.getPhoneNumber();
        this.address = user.getAddress();
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public UserDTO(String firstName, String lastname, String email, String phoneNumber, String address) {
        this.firstName = firstName;
        this.lastname = lastname;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }

    public UserDTO() {}
}
