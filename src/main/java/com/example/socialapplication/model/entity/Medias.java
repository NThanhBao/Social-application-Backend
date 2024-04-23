package com.example.socialapplication.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Data
@Table(name = "medias")
public class Medias {
    @Id

    @Column(name = "id", columnDefinition = "CHAR(36)")

    private String id;

    public Medias() {
        this.id = UUID.randomUUID().toString();
    }

    @Column(name = "base_name")
    private String baseName;

    @Column(name = "public_url")
    private String publicUrl;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "post_id")
    private Posts postsId;

    public Medias(String id) {
        this.id = id;
    }
    public void setPostsId(Posts postId) {
        this.postsId = postId;
    }
}
