package com.example.workout.Domain.DTO;

import java.io.Serializable;

public class LoginDTO implements Serializable {
    private String username;
    private String password;

    public LoginDTO(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
