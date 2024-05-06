package com.example.socialapplication.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "shares")
public class SharesPosts {

    @Id
    @Column(length = 36)
    private String id;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Posts postId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users createBy;

    @Column(name = "created_at")
    private Timestamp createAt;

    public SharesPosts() {
        this.id = UUID.randomUUID().toString();
    }

    @PrePersist
    protected void onCreate() {
        createAt = new Timestamp(new Date().getTime());
    }
}
