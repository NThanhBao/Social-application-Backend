package com.example.socialapplication.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "saved_posts")
public class SavedPost {

    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "post_id", nullable = false, length = 36)
    private String postId;

    @Column(name = "user_id", nullable = false, length = 36)
    private String userId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

}