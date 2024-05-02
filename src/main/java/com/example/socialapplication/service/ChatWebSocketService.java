package com.example.socialapplication.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Service;

@Service
public class ChatWebSocketService {

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    public void sendChatMessage(String username, String message) {
        messagingTemplate.convertAndSend("/topic/public", username + ": " + message);
    }

    public String getUsernameFromSession(StompHeaderAccessor accessor) {
        return (String) accessor.getSessionAttributes().get("username");
    }
}
