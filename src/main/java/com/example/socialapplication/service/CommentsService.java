package com.example.socialapplication.service;


import com.example.socialapplication.model.dto.CommentsDto;
import com.example.socialapplication.model.entity.Comments;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface CommentsService {
    Comments saveComment(CommentsDto comment);
    Page<Comments> getCommentsByPostId(String postId, Pageable pageable);
    void deleteComment(UUID commentId);
    void updateComments(CommentsDto commentsDto);
}
