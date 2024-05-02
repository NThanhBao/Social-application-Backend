package com.example.socialapplication.model.dto;

import com.example.socialapplication.model.entity.Posts;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class CommentsDto {
    private String id;

    private String content;

    private int totalLike;

    private Timestamp createAt;

    private String postId;

    private String createBy;

    public void incrementTotalComment(Posts posts) {
        if (posts != null) {
            int currentTotalComment = posts.getTotalComment();
            posts.setTotalComment(currentTotalComment + 1);
        }
    }
}
