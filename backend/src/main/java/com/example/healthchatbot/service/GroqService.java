package com.example.healthchatbot.service;

import com.example.healthchatbot.dto.ChatRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GroqService {
    
    @Value("${groq.api.key:}")
    private String apiKey;
    
    @Value("${groq.api.url:https://api.groq.com/openai/v1/chat/completions}")
    private String apiUrl;
    
    @Value("${groq.model:llama-3.1-8b-instant}")
    private String model;
    
    private final WebClient webClient;
    
    public GroqService() {
        this.webClient = WebClient.builder()
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
    
    public String getExerciseAdvice(ChatRequest request) {
        try {
            String prompt = buildPrompt(request);
            
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);
            
            List<Map<String, String>> messages = new ArrayList<>();
            Map<String, String> userMessage = new HashMap<>();
            userMessage.put("role", "user");
            userMessage.put("content", prompt);
            messages.add(userMessage);
            
            requestBody.put("messages", messages);
            requestBody.put("temperature", 0.7);
            requestBody.put("max_tokens", 300);
            
            String url = apiUrl;
            WebClient.RequestBodySpec requestSpec = webClient.post()
                    .uri(url);
            
            // Add authorization header
            if (apiKey == null || apiKey.isEmpty() || apiKey.equals("YOUR_GROQ_API_KEY_HERE")) {
                return "Groq API key is not configured. Please:\n1. Get free API key at https://console.groq.com/\n2. Update groq.api.key in application.properties\n3. Restart backend";
            }
            requestSpec.header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey);
            
            Map<String, Object> response = requestSpec
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
            
            if (response == null) {
                return "Sorry, I cannot generate advice at this time. Please try again later.";
            }
            
            // Check for error
            if (response.containsKey("error")) {
                Map<String, Object> error = (Map<String, Object>) response.get("error");
                String errorMessage = error.get("message") != null ? error.get("message").toString() : "Unknown error";
                return "AI service error: " + errorMessage + ". Please try again later.";
            }
            
            // Parse Groq response format
            if (response.containsKey("choices")) {
                List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
                if (!choices.isEmpty()) {
                    Map<String, Object> choice = choices.get(0);
                    if (choice.containsKey("message")) {
                        Map<String, Object> message = (Map<String, Object>) choice.get("message");
                        if (message.containsKey("content")) {
                            return (String) message.get("content");
                        }
                    }
                }
            }
            
            return "Sorry, I cannot generate advice at this time. Please try again later.";
            
        } catch (org.springframework.web.reactive.function.client.WebClientResponseException e) {
            System.err.println("HTTP Error: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
            return "AI service error: " + e.getStatusCode() + " - " + e.getMessage() + ". Please check your API key.";
        } catch (Exception e) {
            System.err.println("Exception in GroqService: " + e.getMessage());
            e.printStackTrace();
            return "An error occurred while connecting to AI: " + e.getMessage() + ". Please try again later.";
        }
    }
    
    private String buildPrompt(ChatRequest request) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("You are a fitness expert. ");
        prompt.append("User needs: ").append(request.getUserNeed()).append(". ");
        
        if (request.getAdditionalInfo() != null && !request.getAdditionalInfo().isEmpty()) {
            prompt.append("Info: ").append(request.getAdditionalInfo()).append(". ");
        }
        
        prompt.append("Provide 3-5 exercise recommendations. ");
        prompt.append("For each exercise, include: name, brief instructions (1-2 sentences), reps, sets. ");
        prompt.append("Keep response concise (under 100 words). Use bullet points. Be direct and practical.");
        
        return prompt.toString();
    }
}

