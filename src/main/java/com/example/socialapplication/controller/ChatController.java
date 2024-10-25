package com.example.socialapplication.controller;

import com.example.socialapplication.model.dto.UsersInfoDto;
import com.example.socialapplication.model.entity.Message;
import com.example.socialapplication.repositories.MessageRepository;
import com.example.socialapplication.service.FollowService;
import com.example.socialapplication.util.annotation.CheckLogin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@CrossOrigin(origins = "http://localhost:3000")
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final MessageRepository messageRepository;
    private final FollowService followService;

    public ChatController(SimpMessagingTemplate messagingTemplate, MessageRepository messageRepository, FollowService followService) {
        this.messagingTemplate = messagingTemplate;
        this.messageRepository = messageRepository;
        this.followService = followService;
    }

    @MessageMapping("/chat/{recipientId}")
    @SendTo("/topic/messages/{recipientId}")
    public void sendMessage(@DestinationVariable String recipientId, Message message) {
        // Lưu tin nhắn vào cơ sở dữ liệu
        messageRepository.save(message);

        // Gửi tin nhắn đến người nhận
        messagingTemplate.convertAndSend("/topic/messages/" + recipientId, message);
    }


    // Hiện danh sách bạn bè
    @CheckLogin
    @GetMapping("/ListFriends")
    public ResponseEntity<List<UsersInfoDto>> getListFriends(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int pageSize,
            @RequestParam(defaultValue = "createAt") String sortName,
            @RequestParam(defaultValue = "DESC") String sortType) {

        Sort.Direction direction = Sort.Direction.fromString(sortType);
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(direction, sortName));

        Page<UsersInfoDto> followerUsers = followService.getListFriends(pageable);
        return ResponseEntity.ok(followerUsers.getContent());
    }

//    Hiện lịch sử tin nhắn
    @CheckLogin
    @GetMapping("/messages")
    public ResponseEntity<List<Message>> getMessages(
            @RequestParam String senderId,
            @RequestParam String recipientId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int pageSize) {

        Pageable pageable = PageRequest.of(page, pageSize);
        Page<Message> messagesPage = messageRepository.findChatHistoryWithPagination(senderId, recipientId, pageable);
        return ResponseEntity.ok(messagesPage.getContent());
    }

}
