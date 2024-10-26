import { Component, OnInit, ViewChild, ElementRef } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-meeting-room',
  templateUrl: './meeting-room.component.html',
  styleUrls: ['./meeting-room.component.css']
})
export class MeetingRoomComponent implements OnInit {
  meetingId: string = '';
  participantName: string = '';
  peerConnection!: RTCPeerConnection;
  localStream!: MediaStream;
  isVideoOn: boolean = false;
  webSocket!: WebSocket;

  @ViewChild('localVideo') localVideo!: ElementRef<HTMLVideoElement>;
  @ViewChild('remoteVideo') remoteVideo!: ElementRef<HTMLVideoElement>;

  constructor(private route: ActivatedRoute) {}

  ngOnInit(): void {
    this.meetingId = this.route.snapshot.paramMap.get('id') || '';
    this.participantName = this.route.snapshot.paramMap.get('participantName') || '';

    this.initializeWebSocketConnection();
    this.initializePeerConnection();
    this.joinMeeting();
  }

  initializeWebSocketConnection() {
    this.webSocket = new WebSocket(`ws://localhost:8080/ws/topic/meeting/${this.meetingId}`);

    this.webSocket.onmessage = (event) => {
      const data = JSON.parse(event.data);
      this.handleSignalingData(data);
    };

    this.webSocket.onerror = (error) => {
      console.error('WebSocket error:', error);
      this.reconnectWebSocket();
    };

    this.webSocket.onclose = () => {
      console.log('WebSocket connection closed');
      this.reconnectWebSocket();
    };
  }

  reconnectWebSocket() {
    setTimeout(() => {
      this.initializeWebSocketConnection();
    }, 5000);
  }

  handleSignalingData(data: any) {
    switch (data.type) {
      case 'offer':
        this.handleOffer(data.message);
        break;
      case 'answer':
        this.handleAnswer(data.message);
        break;
      case 'ice-candidate':
        this.handleRemoteCandidate(data.message);
        break;
      case 'toggle-video':
        console.log(`Participant ${data.sender} toggled video: ${data.message}`);
        break;
      case 'mute-audio':
        console.log(`Participant ${data.sender} muted audio.`);
        break;
      case 'unmute-audio':
        console.log(`Participant ${data.sender} unmuted audio.`);
        break;
      case 'join':
        console.log(`Participant ${data.sender} joined the meeting: ${data.message}`);
        break;
      case 'leave':
        console.log(`Participant ${data.sender} left the meeting: ${data.message}`);
        break;
      default:
        console.error('Unknown signaling data type:', data.type);
    }
  }

  initializePeerConnection() {
    const config: RTCConfiguration = {
      iceServers: [{ urls: 'stun:stun.l.google.com:19302' }]
    };

    this.peerConnection = new RTCPeerConnection(config);

    this.peerConnection.onicecandidate = (event) => {
      if (event.candidate) {
        this.sendSignalingData('ice-candidate', event.candidate);
      }
    };

    this.peerConnection.ontrack = (event) => {
      this.remoteVideo.nativeElement.srcObject = event.streams[0];
    };
  }

  joinMeeting() {
    navigator.mediaDevices.getUserMedia({ video: true, audio: true })
      .then((stream) => {
        this.localStream = stream;
        this.isVideoOn = true;
        this.attachLocalStream();
        this.addLocalTracksToPeerConnection();
        this.sendSignalingData('join', `${this.participantName} has joined`);
      })
      .catch((error) => {
        console.error('Error accessing media devices:', error);
      });
  }

  attachLocalStream() {
    this.localVideo.nativeElement.srcObject = this.localStream;
  }

  addLocalTracksToPeerConnection() {
    this.localStream.getTracks().forEach(track => {
      this.peerConnection.addTrack(track, this.localStream);
    });
  }

  handleOffer(offer: RTCSessionDescriptionInit) {
    this.peerConnection.setRemoteDescription(new RTCSessionDescription(offer))
      .then(() => this.peerConnection.createAnswer())
      .then((answer) => {
        this.peerConnection.setLocalDescription(answer);
        this.sendSignalingData('answer', answer);
      })
      .catch(error => {
        console.error('Error handling offer:', error);
      });
  }

  handleAnswer(answer: RTCSessionDescriptionInit) {
    this.peerConnection.setRemoteDescription(answer)
      .catch(error => {
        console.error('Error setting remote description:', error);
      });
  }

  handleRemoteCandidate(candidate: RTCIceCandidateInit) {
    this.peerConnection.addIceCandidate(new RTCIceCandidate(candidate))
      .catch(error => {
        console.error('Error adding remote ICE candidate:', error);
      });
  }

  sendSignalingData(type: string, data: any) {
    const signalingData = { type, meetingId: this.meetingId, sender: this.participantName, message: data };
    this.webSocket.send(JSON.stringify(signalingData));
  }

  toggleMute() {
    const audioTracks = this.localStream.getAudioTracks();
    if (audioTracks.length > 0) {
      audioTracks[0].enabled = !audioTracks[0].enabled;
      const action = audioTracks[0].enabled ? 'unmute-audio' : 'mute-audio';
      this.sendSignalingData(action, `${this.participantName}`);
    }
  }

  toggleVideo() {
    this.isVideoOn ? this.stopVideo() : this.startVideo();
    this.sendSignalingData('toggle-video', this.isVideoOn ? 'Video On' : 'Video Off');
  }

  startVideo() {
    navigator.mediaDevices.getUserMedia({ video: true, audio: true })
      .then((stream) => {
        this.localStream = stream;
        this.isVideoOn = true;
        this.attachLocalStream();
        this.addLocalTracksToPeerConnection();
      })
      .catch((error) => {
        console.error('Error accessing media devices:', error);
      });
  }

  stopVideo() {
    this.localStream.getTracks().forEach(track => {
      if (track.kind === 'video') {
        track.stop();
        this.isVideoOn = false;
      }
    });
  }

  leaveMeeting() {
    this.localStream.getTracks().forEach(track => track.stop());
    this.peerConnection.close();
    this.sendSignalingData('leave', `${this.participantName} has left the meeting`);
  }
}
