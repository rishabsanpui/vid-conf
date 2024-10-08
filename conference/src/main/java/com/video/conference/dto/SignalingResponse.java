package com.video.conference.dto;

public class SignalingResponse {
    private String type;
    private String sender;
    private String message;

    public SignalingResponse(String type, String sender, String message) {
        this.type = type;
        this.sender = sender;
        this.message = message;
    }

    // Getters and Setters
    public String getType() {
        return type;
    }

    public String getSender() {
        return sender;
    }

    public String getMessage() {
        return message;
    }
}

