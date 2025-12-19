package com.example.myapplication.Domain.DTO;

import java.io.Serializable;

public class LoginResponse implements Serializable {
    private String userId;
    private String fullName;
    private String role; // "USER" or "ADMIN"

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getRole() {
        return role != null ? role : "USER";
    }

    public void setRole(String role) {
        this.role = role;
    }
}
