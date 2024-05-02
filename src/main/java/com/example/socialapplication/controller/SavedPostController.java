package com.example.socialapplication.controller;

import com.example.socialapplication.model.entity.SavedPost;
import com.example.socialapplication.service.SavedPostService;
import com.example.socialapplication.util.annotation.CheckLogin;
import com.example.socialapplication.util.exception.NotFoundException;
import com.example.socialapplication.util.exception.UnauthorizedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
public class SavedPostController {

    private final SavedPostService savedPostService;

    public SavedPostController(SavedPostService savedPostService) {
        this.savedPostService = savedPostService;
    }

    @CheckLogin
    @PostMapping("/savePost/{postId}")
    public ResponseEntity<String> savePost(@PathVariable String postId, Authentication authentication) {
        String currentUsername = authentication.getName();
        SavedPost savedPost = savedPostService.savePost(postId, currentUsername);
        return ResponseEntity.ok("Bài đăng đã được lưu thành công.");
    }

    @CheckLogin
    @DeleteMapping("/{savePostId}")
    public ResponseEntity<String> deleteSavedPost(@PathVariable String savePostId, Authentication authentication) {
        String currentUsername = authentication.getName();
        savedPostService.deleteSavedPost(savePostId, currentUsername);
        return ResponseEntity.ok("Bài viết đã được xóa khỏi danh sách đã lưu.");
    }

    @CheckLogin
    @GetMapping("/current-user")
    public ResponseEntity<List<SavedPost>> getSavedPostsByCurrentUser(Authentication authentication) {
        String currentUsername = authentication.getName();
        List<SavedPost> savedPosts = savedPostService.getSavedPostByCurrentUser(currentUsername);
        return ResponseEntity.ok(savedPosts);
    }

    @ExceptionHandler({NotFoundException.class, AuthenticationCredentialsNotFoundException.class, UnauthorizedException.class})
    public ResponseEntity<String> handleException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<SavedPost>> getSavedPostsByUserId(@PathVariable String userId) {
        try {
            List<SavedPost> savedPosts = savedPostService.getSavedPostsByUserId(userId);
            return ResponseEntity.ok(savedPosts);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }
}

