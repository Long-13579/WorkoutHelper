package com.example.healthchatbot.controller;

import com.example.healthchatbot.dto.ChatRequest;
import com.example.healthchatbot.dto.ChatResponse;
import com.example.healthchatbot.service.GeminiService;
import com.example.healthchatbot.service.GroqService;
import com.example.healthchatbot.service.HuggingFaceService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*")
public class ChatController {
    
    @Autowired(required = false)
    private GeminiService geminiService;
    
    @Autowired(required = false)
    private GroqService groqService;
    
    @Autowired(required = false)
    private HuggingFaceService huggingFaceService;
    
    @Value("${ai.provider:groq}")
    private String aiProvider;
    
    @PostMapping("/advice")
    public ResponseEntity<ChatResponse> getExerciseAdvice(@Valid @RequestBody ChatRequest request) {
        try {
            String advice = null;
            
            // Select AI provider based on configuration
            switch (aiProvider.toLowerCase()) {
                case "groq":
                    if (groqService != null) {
                        advice = groqService.getExerciseAdvice(request);
                    } else {
                        advice = "Groq service is not configured. Please check your API key.";
                    }
                    break;
                case "huggingface":
                case "hf":
                    if (huggingFaceService != null) {
                        advice = huggingFaceService.getExerciseAdvice(request);
                    } else {
                        advice = "Hugging Face service is not configured.";
                    }
                    break;
                case "gemini":
                default:
                    if (geminiService != null) {
                        advice = geminiService.getExerciseAdvice(request);
                    } else {
                        advice = "Gemini service is not configured. Please check your API key.";
                    }
                    break;
            }
            
            ChatResponse response = new ChatResponse();
            response.setAdvice(advice);
            response.setExercises(advice);
            response.setMessage("Success");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ChatResponse errorResponse = new ChatResponse();
            errorResponse.setMessage("Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}

