package com.video.conference.dto;

import java.util.HashSet;
import java.util.Set;

public class CallSession {
    private String sessionId;
    private String host;
    private Set<String> participants;

    public CallSession(String sessionId, String host) {
        this.sessionId = sessionId;
        this.host = host;
        this.participants = new HashSet<>();
        this.participants.add(host);
    }

    // Getters and Setters

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Set<String> getParticipants() {
        return participants;
    }

    public void setParticipants(Set<String> participants) {
        this.participants = participants;
    }

    // Add a participant
    public void addParticipant(String participant) {
        this.participants.add(participant);
    }

    // Remove a participant
    public void removeParticipant(String participant) {
        this.participants.remove(participant);
    }
}
