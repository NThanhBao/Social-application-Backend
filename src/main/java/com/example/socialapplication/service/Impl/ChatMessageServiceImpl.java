//package com.example.socialapplication.service.Impl;
//
//import com.example.socialapplication.model.entity.ChatMessage;
//import com.example.socialapplication.repositories.ChatMessageRepository;
//import com.example.socialapplication.service.ChatMessageService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//public class ChatMessageServiceImpl implements ChatMessageService {
//
//    @Autowired
//    private ChatMessageRepository messageRepository;
//
//    @Override
//    public void addMessage(ChatMessage message) {
//        messageRepository.save(message);
//    }
//
//    @Override
//    public List<ChatMessage> getAllMessages() {
//        return messageRepository.findAll();
//    }
//}