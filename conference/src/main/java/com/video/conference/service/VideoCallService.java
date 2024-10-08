package com.video.conference.service;

import com.video.conference.dto.CallSession;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class VideoCallService {

    // In-memory storage for sessions (you can later replace this with a database)
    private final Map<String, CallSession> sessionStore = new HashMap<>();

    // In-memory storage for ICE candidates and SDP for each session
    private final Map<String, Object> iceCandidatesStore = new HashMap<>();
    private final Map<String, Object> sdpStore = new HashMap<>();

    // Start a new video call session
    public CallSession startSession(String host) {
        String sessionId = UUID.randomUUID().toString(); // Generate unique session ID
        CallSession session = new CallSession(sessionId, host);
        sessionStore.put(sessionId, session);
        return session;
    }

    // Join an existing session
    public CallSession joinSession(String sessionId, String participant) {
        CallSession session = sessionStore.get(sessionId);
        if (session == null) {
            throw new IllegalArgumentException("Session not found!");
        }
        session.addParticipant(participant);
        return session;
    }

    // Leave a session
    public boolean leaveSession(String sessionId, String participant) {
        CallSession session = sessionStore.get(sessionId);
        if (session == null) {
            throw new IllegalArgumentException("Session not found!");
        }
        session.removeParticipant(participant);
        if (session.getParticipants().isEmpty()) {
            sessionStore.remove(sessionId); // Remove session if no participants are left
            return false; // Session ended as all participants left
        }
        return true; // Participant left successfully
    }

    // End the session
    public boolean endSession(String sessionId) {
        if (sessionStore.containsKey(sessionId)) {
            sessionStore.remove(sessionId);
            iceCandidatesStore.remove(sessionId);
            sdpStore.remove(sessionId);
            return true; // Session ended successfully
        } else {
            throw new IllegalArgumentException("Session not found!");
        }
    }

    // Get session details
    public CallSession getSessionDetails(String sessionId) {
        CallSession session = sessionStore.get(sessionId);
        if (session == null) {
            throw new IllegalArgumentException("Session not found!");
        }
        return session;
    }

    // Handle ICE candidates for the session
    public void handleIceCandidate(String sessionId, Object candidate) {
        if (!sessionStore.containsKey(sessionId)) {
            throw new IllegalArgumentException("Session not found!");
        }
        iceCandidatesStore.put(sessionId, candidate); // Save ICE candidate for the session
        // You can also broadcast the candidate to the peers in the session
    }

    // Handle SDP offer/answer for the session
    public void handleSdp(String sessionId, String type, Object sdp) {
        if (!sessionStore.containsKey(sessionId)) {
            throw new IllegalArgumentException("Session not found!");
        }
        sdpStore.put(sessionId, sdp); // Save SDP for the session
        // You can broadcast the SDP offer/answer to other peers in the session
    }

    // Get stored ICE candidates for the session (optional method if needed)
    public Object getIceCandidates(String sessionId) {
        return iceCandidatesStore.get(sessionId);
    }

    // Get stored SDP offer/answer for the session (optional method if needed)
    public Object getSdp(String sessionId) {
        return sdpStore.get(sessionId);
    }
}
