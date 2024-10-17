package com.example.socialapplication.service.Impl;

import com.example.socialapplication.service.ChatService;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Service;

@Service
public class ChatServiceImpl implements ChatService {

    private final SimpMessageSendingOperations messagingTemplate;

    public ChatServiceImpl(SimpMessageSendingOperations messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    // Gửi tin nhắn tới nhóm chung (public chat)
    @Override
    public void sendChatMessage(String username, String message) {
        messagingTemplate.convertAndSend("/topic/public", username + ": " + message);
    }

    // Gửi tin nhắn riêng (private chat)
    @Override
    public void sendPrivateMessage(String recipient, String sender, String message) {
        messagingTemplate.convertAndSendToUser(recipient, "/private", sender + ": " + message);
    }

    // Lấy username từ session
    @Override
    public String getUsernameFromSession(StompHeaderAccessor accessor) {
        return (String) accessor.getSessionAttributes().get("username");
    }
}
