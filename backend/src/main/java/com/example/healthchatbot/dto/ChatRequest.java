package com.example.healthchatbot.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChatRequest {
    @NotBlank(message = "Exercise needs cannot be empty")
    private String userNeed;
    
    private String additionalInfo; // Additional information such as age, fitness level, etc.
}

