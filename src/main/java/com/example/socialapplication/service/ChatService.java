package com.example.socialapplication.service;

import org.springframework.messaging.simp.stomp.StompHeaderAccessor;

public interface ChatService {

    void sendChatMessage(String username, String message);

    void sendPrivateMessage(String recipient, String sender, String message);

    String getUsernameFromSession(StompHeaderAccessor accessor);

}
