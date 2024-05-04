package com.example.socialapplication.model.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@Table(name = "posts")
public class Posts {
    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private String id;

    public Posts() {
        this.id = UUID.randomUUID().toString();
    }

    private String title;

    @Column
    @NotNull
    private String body;

    private String status;

    @Column(name = "total_like")
    private int totalLike;

    @Column(name = "total_comment")
    private int totalComment;

    @Column(name = "total_share")
    private int totalShare;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private Users userId;

    @Column(name = "created_at")
    private Timestamp createAt;

    @JsonManagedReference
    @OneToMany(mappedBy = "postsId", cascade = CascadeType.ALL, fetch = FetchType.EAGER )
    private List<Medias> medias;

    @Override
    public String toString() {
        return "Posts{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", body='" + body + '\'' +
                ", status='" + status + '\'' +
                ", totalLike=" + totalLike +
                ", totalComment=" + totalComment +
                ", createAt=" + createAt +
                '}';
    }
}
