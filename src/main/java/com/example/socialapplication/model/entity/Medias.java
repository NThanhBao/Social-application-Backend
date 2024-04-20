package com.example.socialapplication.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Data
@Table(name = "medias")
public class Medias {
    @Id
    private String id;

    @Column(name = "base_name")
    private String baseName;

    @Column(name = "public_url")
    private String publicUrl;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Posts postsId;

    public Medias() {
        this.id = UUID.randomUUID().toString();
    }
}
