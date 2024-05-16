package com.example.socialapplication.controller;

import com.example.socialapplication.model.dto.UsersDto;
import com.example.socialapplication.model.dto.UsersInfoDto;
import com.example.socialapplication.model.entity.Comments;
import com.example.socialapplication.model.entity.OTP_ResetPassword;
import com.example.socialapplication.model.entity.Users;
import com.example.socialapplication.service.CommentsService;
import com.example.socialapplication.service.OTPService;
import com.example.socialapplication.service.PostsService;
import com.example.socialapplication.service.UsersService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/admin")
public class AdminController {

    private final UsersService userService;
    private final CommentsService commentsService;
    private final PostsService postsService;
    private final OTPService otpService;

    public AdminController(UsersService userService, CommentsService commentsService, PostsService postsService, OTPService otpService) {
        this.userService = userService;
        this.commentsService = commentsService;
        this.postsService = postsService;
        this.otpService = otpService;
    }

    @GetMapping("/allUsers")
    public ResponseEntity<List<Users>> getAllUsers(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "1000") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Users> users = userService.getAllUsers(pageable);
        return ResponseEntity.ok().body(users.getContent());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserById(@PathVariable String userId) {
        Users userDTO = userService.getUserByIds(userId);
        if (userDTO == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(userDTO);
    }

    @GetMapping("/allPostsCount")
    public ResponseEntity<Integer> getNumberOfPosts() {
        int numberOfPosts = postsService.getNumberOfPosts();
        return new ResponseEntity<>(numberOfPosts, HttpStatus.OK);
    }

    @GetMapping("/allCommentsCount")
    public ResponseEntity<Integer> getNumberOfComments() {
        int numberOfPosts = commentsService.getNumberOfComments();
        return new ResponseEntity<>(numberOfPosts, HttpStatus.OK);
    }

    @GetMapping("/allComments")
    public ResponseEntity<List<Comments>> getAllCommentsOnAllPosts(@RequestParam(defaultValue = "0") int page,
                                                                   @RequestParam(defaultValue = "100") int pageSize,
                                                                   @RequestParam(defaultValue = "createAt") String sortName,
                                                                   @RequestParam(defaultValue = "DESC") String sortType)  {
        try {
            Sort.Direction direction = sortType.equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC;
            Pageable pageable = PageRequest.of(page, pageSize, Sort.by(direction, sortName));
            Page<Comments> commentsPage = commentsService.getAllCommentsOnAllPosts(pageable);
            return ResponseEntity.ok().body(commentsPage.getContent());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{userId}/disable")
    public ResponseEntity<String> disableUser(@PathVariable String userId) {
        return userService.userDisableTypeById(userId);
    }

    @PutMapping("/{userId}/enable")
    public ResponseEntity<String> enableUser(@PathVariable String userId) {
        return userService.userEnableTypeById(userId);
    }

    @GetMapping("/users-with-otp")
    public ResponseEntity<List<OTP_ResetPassword>> getUsersWithOTP(@RequestParam(defaultValue = "0") int page,
                                                                   @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<OTP_ResetPassword> usersWithOTP = otpService.findAllUsersWithOTP(pageable);
        return new ResponseEntity<>(usersWithOTP.getContent(), HttpStatus.OK);
    }
}
