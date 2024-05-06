package com.example.socialapplication.service;

import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Service;

@Service
public class ChatWebSocketService {

    private final SimpMessageSendingOperations messagingTemplate;

    public ChatWebSocketService(SimpMessageSendingOperations messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void sendChatMessage(String username, String message) {
        messagingTemplate.convertAndSend("/topic/public", username + ": " + message);
    }

    public String getUsernameFromSession(StompHeaderAccessor accessor) {
        return (String) accessor.getSessionAttributes().get("username");
    }
}
