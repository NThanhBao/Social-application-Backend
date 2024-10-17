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

    private String sender;

    private String content;

    private String recipient;

    private MessageType type; // Sử dụng MessageType

    private Timestamp createdAt;

    public Message(Long id, String sender, String content, String recipient, MessageType type, Timestamp createdAt) {
        this.id = id;
        this.sender = sender;
        this.content = content;
        this.recipient = recipient;
        this.type = type; // Khởi tạo type
        this.createdAt = createdAt;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = new Timestamp(new Date().getTime());
    }
}