package com.example.socialapplication.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;
import java.util.UUID;

@Data
@Entity
@Table(name = "comments")
public class Comments {

    @Id
    private String id;

    private String content;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Posts postId;

    @Column(name = "total_like")
    private int totalLike;

    @Column(name = "created_at")
    private Timestamp createAt;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private Users createBy;

    public Comments() {
        this.id = UUID.randomUUID().toString();
    }
}

