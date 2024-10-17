package com.example.socialapplication.controller;

import com.example.socialapplication.model.dto.UsersInfoDto;
import com.example.socialapplication.model.entity.Message;
import com.example.socialapplication.repositories.MessageRepository;
import com.example.socialapplication.service.ChatService;
import com.example.socialapplication.service.FollowService;
import com.example.socialapplication.util.annotation.CheckLogin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@Controller
@CrossOrigin(origins = "http://localhost:3000")
public class ChatController {

    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);

    private final MessageRepository messageRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final FollowService followService;
    private final ChatService chatService;

    @Autowired
    public ChatController(MessageRepository messageRepository, SimpMessagingTemplate messagingTemplate,
                          FollowService followService, ChatService chatService) {
        this.messageRepository = messageRepository;
        this.messagingTemplate = messagingTemplate;
        this.followService = followService;
        this.chatService = chatService;
    }

    // Xử lý việc gửi tin nhắn
    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public Message sendMessage(@Payload Message chatMessage) {
        return chatMessage;
    }

    // Thêm người dùng vào chat
    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public Message addUser(@Payload Message chatMessage,
                           SimpMessageHeaderAccessor headerAccessor) {
        // Thêm tên người dùng vào session của WebSocket
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        return chatMessage;
    }

    // Gửi tin nhắn riêng
    @PostMapping("/chat/privateMessage")
    public ResponseEntity<Message> sendPrivateMessage(@RequestBody Message chatMessage) {
        if (isInvalidMessage(chatMessage)) {
            logger.error("Invalid message: {}", chatMessage);
            return ResponseEntity.badRequest().build();
        }

        // Lưu tin nhắn vào cơ sở dữ liệu
        Message savedMessage = messageRepository.save(chatMessage);

        // Gửi tin nhắn đến người nhận qua WebSocket
        messagingTemplate.convertAndSendToUser(chatMessage.getRecipient(), "/private", savedMessage);
        logger.info("Sending private message to user: {}", chatMessage.getRecipient());

        return ResponseEntity.ok(savedMessage); // Trả về tin nhắn đã lưu
    }

    private boolean isInvalidMessage(Message chatMessage) {
        return chatMessage.getSender() == null || chatMessage.getRecipient() == null || chatMessage.getContent() == null;
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

    // Lấy lịch sử chat giữa hai người
    @GetMapping("/chat/history")
    public ResponseEntity<List<Message>> getChatHistory(
            @RequestParam String currentUser,
            @RequestParam String recipient,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int pageSize) {

        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(Sort.Direction.ASC, "createdAt"));

        // Gọi phương thức trong repository
        Page<Message> messages = messageRepository.findChatHistory(currentUser, recipient, pageable);

        return ResponseEntity.ok(messages.getContent());
    }

}
