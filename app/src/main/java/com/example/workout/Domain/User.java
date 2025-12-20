package com.example.myapplication.Domain;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class User implements Serializable {
    private String id;
    private String username;
    private String password;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String dateOfBirth;
    private String gender;
    private String role; // "USER" or "ADMIN"

    public User(String id, String username, String password, String fullName, String email,
                String phoneNumber, String dateOfBirth, String gender, String role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.role = role;
    }
    
    public User(String id, String username, String password, String fullName, String email,
                String phoneNumber, String dateOfBirth, String gender) {
        this(id, username, password, fullName, email, phoneNumber, dateOfBirth, gender, "USER");
    }

    public User() {}

    // Getter & Setter cho tất cả các thuộc tính

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
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

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getRole() {
        return role != null ? role : "USER";
    }

    public void setRole(String role) {
        this.role = role;
    }

    @NonNull
    @Override
    public String toString() {
        return "UserId: " + id
                + "\n Username: " + username
                + "\n Password: " + password
                + "\n FullName: " + fullName
                + "\n Email: " + email
                + "\n Phone: " + phoneNumber
                + "\n DateOfBirth: " + dateOfBirth
                + "\n Gender: " + gender;
    }
}