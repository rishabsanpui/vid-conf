package com.video.conference.controller;

import com.video.conference.dto.SignalingRequest;
import com.video.conference.dto.SignalingResponse;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@Controller
public class SignallingController {

    private final SimpMessagingTemplate messagingTemplate;

    public SignallingController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    // Handles offer signal to start the connection
    @MessageMapping("/offer")
    public void handleOffer(SignalingRequest request) {
        SignalingResponse response = new SignalingResponse("offer", request.getSender(), request.getMessage());
        messagingTemplate.convertAndSend("/topic/offer", response);
    }

    // Handles answer signal to respond to the connection offer
    @MessageMapping("/answer")
    public void handleAnswer(SignalingRequest request) {
        SignalingResponse response = new SignalingResponse("answer", request.getSender(), request.getMessage());
        messagingTemplate.convertAndSend("/topic/answer", response);
    }

    // Handles ICE candidate exchange for P2P connection setup
    @MessageMapping("/ice-candidate")
    public void handleIceCandidates(SignalingRequest request) {
        SignalingResponse response = new SignalingResponse("ice-candidate", request.getSender(), request.getMessage());
        messagingTemplate.convertAndSend("/topic/ice-candidate", response);
    }

    // Handles participants joining a meeting
    @MessageMapping("/join")
    public void handleJoin(SignalingRequest request) {
        SignalingResponse response = new SignalingResponse("join", request.getSender(), request.getMessage());
        messagingTemplate.convertAndSend("/topic/join", response);
    }

    // Handles participants leaving a meeting
    @MessageMapping("/leave")
    public void handleLeave(SignalingRequest request) {
        SignalingResponse response = new SignalingResponse("leave", request.getSender(), request.getMessage());
        messagingTemplate.convertAndSend("/topic/leave", response);
    }

    // Handles video toggling (on/off)
    @MessageMapping("/toggle-video")
    public void handleVideoToggle(SignalingRequest request) {
        SignalingResponse response = new SignalingResponse("toggle-video", request.getSender(), request.getMessage());
        messagingTemplate.convertAndSend("/topic/toggle-video", response);
    }

    // Handles muting of a participant
    @MessageMapping("/mute-audio")
    public void handleMuteAudio(SignalingRequest request) {
        SignalingResponse response = new SignalingResponse("mute-audio", request.getSender(), request.getMessage() + " has muted their audio.");
        messagingTemplate.convertAndSend("/topic/audio-status", response);
    }

    // Handles unmuting of a participant
    @MessageMapping("/unmute-audio")
    public void handleUnmuteAudio(SignalingRequest request) {
        SignalingResponse response = new SignalingResponse("unmute-audio", request.getSender(), request.getMessage() + " has unmuted their audio.");
        messagingTemplate.convertAndSend("/topic/audio-status", response);
    }
}
