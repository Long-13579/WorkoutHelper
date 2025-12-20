package com.example.myapplication.Domain;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ChatHistory implements Serializable {
    private String id;
    private String userNeed;
    private String additionalInfo;
    private String response;
    private long timestamp;

    public ChatHistory() {
        this.id = String.valueOf(System.currentTimeMillis());
        this.timestamp = System.currentTimeMillis();
    }

    public ChatHistory(String userNeed, String additionalInfo, String response) {
        this.id = String.valueOf(System.currentTimeMillis());
        this.userNeed = userNeed;
        this.additionalInfo = additionalInfo;
        this.response = response;
        this.timestamp = System.currentTimeMillis();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getFormattedDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }

    public String getPreview() {
        if (userNeed != null && !userNeed.isEmpty()) {
            return userNeed.length() > 50 ? userNeed.substring(0, 50) + "..." : userNeed;
        }
        return "No preview";
    }
}

