package com.example.myapplication.Domain.DTO;

public class ChatRequest {
    private String userNeed;
    private String additionalInfo;

    public ChatRequest() {
    }

    public ChatRequest(String userNeed, String additionalInfo) {
        this.userNeed = userNeed;
        this.additionalInfo = additionalInfo;
    }

    public String getUserNeed() {
        return userNeed;
    }

    public void setUserNeed(String userNeed) {
        this.userNeed = userNeed;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }
}

