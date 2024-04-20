package com.example.socialapplication.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Data
@Table(name = "posts")
public class Posts {
    @Id
    private String id;

    private String title;

    private String body;

    private String status;

    @Column(name = "total_like")
    private int totalLike;

    @Column(name = "total_comment")
    private int totalComment;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private Users usersId;

    @Column(name = "created_at")
    private Timestamp createAt;

    public Posts() {
        this.id = UUID.randomUUID().toString();
    }
}
