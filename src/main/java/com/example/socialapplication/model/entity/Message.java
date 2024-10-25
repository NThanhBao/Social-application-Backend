package com.example.socialapplication.model.entity;

import com.example.socialapplication.model.entity.Enum.MessageType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sender; // ID người gửi
    private String recipient; // ID người nhận
    private String content; // Nội dung tin nhắn

    @Enumerated(EnumType.STRING)
    private MessageType type;

    private Timestamp createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = new Timestamp(new Date().getTime());
    }
}