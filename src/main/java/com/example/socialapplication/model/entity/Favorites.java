package com.example.socialapplication.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

@Entity
@Data
@Table(name = "favorites")
public class Favorites {
    @Id
    @ManyToOne
    @JoinColumn(name = "post_id")
    private Posts postsId;

    @ManyToOne
    @JoinColumn(name = "user_id", unique = true)
    private Users usersId;

    @Column(name = "created_at")
    private Timestamp createAt;
}
