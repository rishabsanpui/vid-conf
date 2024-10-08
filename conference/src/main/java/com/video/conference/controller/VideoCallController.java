package com.video.conference.controller;

import com.video.conference.dto.CallSession;
import com.video.conference.service.VideoCallService;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/video-call")
@CrossOrigin(origins = "http://localhost:4200") // Allow requests from Angular app
public class VideoCallController {

    private final VideoCallService videoCallService;

    public VideoCallController(VideoCallService videoCallService) {
        this.videoCallService = videoCallService;
    }

    @PostMapping("/start")
    public ResponseEntity<CallSession> startCall(@RequestParam String host) {
        CallSession session = videoCallService.startSession(host);
        return ResponseEntity.ok(session);
    }

    @PostMapping("/join")
    public ResponseEntity<CallSession> joinCall(@RequestParam String sessionId, @RequestParam String participant) {
        System.out.println("Session ID: " + sessionId + ", Participant: " + participant);
        CallSession session = videoCallService.joinSession(sessionId, participant);
        return ResponseEntity.ok(session);
    }



    @MessageMapping("/signal")
    @SendTo("/topic/signal")
    public Object handleSignal(Object signalData) {
        return signalData;
    }
}
