package com.example.socialapplication.controller;

import com.example.socialapplication.model.dto.SharesPostDto;
import com.example.socialapplication.model.entity.SharesPosts;
import com.example.socialapplication.service.SharesPostService;
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
@RequestMapping("/shares")
public class SharesPostsController {

    private final SharesPostService sharesPostService;

    public SharesPostsController(SharesPostService sharesPostService) {
        this.sharesPostService = sharesPostService;
    }

    @CheckLogin
    @PostMapping("/post/{postId}")
    public ResponseEntity<String> createSharePost(@PathVariable String postId, Authentication authentication) {
        String currentUsername = authentication.getName();
        SharesPostDto sharesPostDto = new SharesPostDto();
        sharesPostDto.setPostId(postId);
        sharesPostDto.setCreateBy(currentUsername);
        SharesPosts createdSharesPost = sharesPostService.createdSharePost(sharesPostDto);
        return ResponseEntity.ok("Bài viết đã được chia sẻ thành công.");
    }


    @CheckLogin
    @DeleteMapping("/{sharesPostId}")
    public ResponseEntity<String> deleteSharedPost(@PathVariable String sharesPostId, Authentication authentication) {
        String currentUsername = authentication.getName();
        sharesPostService.deleteSharedPost(sharesPostId, currentUsername);
        return ResponseEntity.ok("Bài viết đã được xóa khỏi danh sách đã chia sẻ.");
    }

    @CheckLogin
    @GetMapping("/current-user")
    public ResponseEntity<List<SharesPosts>> getSharedPostsByCurrentUser(Authentication authentication) {
        String currentUsername = authentication.getName();
        List<SharesPosts> sharesPosts = sharesPostService.getSharedPostByCurrentUser(currentUsername);
        return ResponseEntity.ok(sharesPosts);
    }

    @ExceptionHandler({NotFoundException.class, AuthenticationCredentialsNotFoundException.class, UnauthorizedException.class})
    public ResponseEntity<String> handleException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<SharesPosts>> getSharedPostsByUserId(@PathVariable String userId) {
        try {
            List<SharesPosts> sharesPosts = sharesPostService.getSharedPostsByUserId(userId);
            return ResponseEntity.ok(sharesPosts);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }
}
