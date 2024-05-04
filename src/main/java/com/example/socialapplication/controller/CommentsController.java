package com.example.socialapplication.controller;

import com.example.socialapplication.model.dto.CommentsDto;
import com.example.socialapplication.model.entity.Comments;
import com.example.socialapplication.service.CommentsService;
import com.example.socialapplication.util.annotation.CheckLogin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/comments")
public class CommentsController {

    private final CommentsService commentsService;

    public CommentsController(CommentsService commentsService) {
        this.commentsService = commentsService;
    }

    @CheckLogin
    @PostMapping("/create")
    public Comments addComment(@RequestParam String postId, @RequestParam String content) {
        CommentsDto comment = new CommentsDto();
        comment.setPostId(postId);
        comment.setContent(content);

        return commentsService.saveComment(comment);
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<Page<Comments>> getCommentsByPostId(@PathVariable String postId,
                                                              @RequestParam(defaultValue = "0") int page,
                                                              @RequestParam(defaultValue = "10") int pageSize,
                                                              @RequestParam(defaultValue = "createAt") String sortName,
                                                              @RequestParam(defaultValue = "DESC") String sortType) {
        try {
            Sort.Direction direction = sortType.equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC;
            Pageable pageable = PageRequest.of(page, pageSize, Sort.by(direction, sortName));
            Page<Comments> commentsPage = commentsService.getCommentsByPostId(postId, pageable);
            return   ResponseEntity.ok().body(commentsPage);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @PutMapping("/update")
    public void update(@RequestParam String id, @RequestParam String content) {
        CommentsDto comment = new CommentsDto();
        comment.setId(id);
        comment.setContent(content);
        commentsService.updateComments(comment);
    }

    @DeleteMapping("/delete/{commentId}")
    public void deleteComment(@PathVariable UUID commentId) {
        commentsService.deleteComment(commentId);
    }
}
