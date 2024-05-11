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

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
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

    @GetMapping("/post/{postId}/shared")
    public ResponseEntity<List<SharesPosts>> getlistUserSharePostsByPostId(@PathVariable String postId) {
        try {
            List<SharesPosts> sharedPosts = sharesPostService.getListSharedPostsByPostId(postId);
            return ResponseEntity.ok(sharedPosts);
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
