package com.example.socialapplication.controller;

import com.example.socialapplication.model.dto.UsersInfoDto;
import com.example.socialapplication.service.FollowService;
import com.example.socialapplication.util.annotation.CheckLogin;
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
@RequestMapping("/auth")
public class FollowController {

    private final FollowService followService;

    public FollowController(FollowService followService) {
        this.followService = followService;
    }

    @CheckLogin
    @PostMapping("/follow/{followingUserId}")
    public ResponseEntity<String> followUser(@PathVariable String followingUserId) {
        followService.followUser(followingUserId);
        return new ResponseEntity<>("User followed successfully", HttpStatus.OK);
    }

    @CheckLogin
    @GetMapping("/followingCount")
    public int getFollowingCount() {
        return followService.getFollowingCount();
    }

    @CheckLogin
    @GetMapping("/followerCount")
    public int getFollowerCount() {
        return followService.getFollowerCount();
    }

    @GetMapping("/followingCount/{username}")
    public int getFollowingCount(@PathVariable String username) {
        return followService.getFollowingCount(username);
    }

    @GetMapping("/followerCount/{username}")
    public int getFollowerCount(@PathVariable String username) {
        return followService.getFollowerCount(username);
    }

    @CheckLogin
    @GetMapping("/ListUsers/following")
    public ResponseEntity<List<UsersInfoDto>> getFollowingUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int pageSize,
            @RequestParam(defaultValue =  "createAt") String sortName,
            @RequestParam(defaultValue = "DESC") String sortType) {

        Sort.Direction direction;

        if (sortType.equalsIgnoreCase("ASC")) {
            direction = Sort.Direction.ASC;
        } else {
            direction = Sort.Direction.DESC;
        }

        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(direction, sortName));

        Page<UsersInfoDto> followingUsers =
                followService.getFollowingListUsers(pageable);
        return ResponseEntity.ok(followingUsers.getContent());
    }

    @CheckLogin
    @GetMapping("/ListFollowing")
    public ResponseEntity<List<UsersInfoDto>> getFollowingNavUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue =  "createAt") String sortName,
            @RequestParam(defaultValue = "DESC") String sortType) {

        Sort.Direction direction;

        if (sortType.equalsIgnoreCase("ASC")) {
            direction = Sort.Direction.ASC;
        } else {
            direction = Sort.Direction.DESC;
        }

        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(direction, sortName));

        Page<UsersInfoDto> followingUsers =
                followService.getFollowingListUsers(pageable);
        return ResponseEntity.ok(followingUsers.getContent());
    }

    @CheckLogin
    @GetMapping("/ListUsers/follower")
    public ResponseEntity<List<UsersInfoDto>> getFollowerUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int pageSize,
            @RequestParam(defaultValue =  "createAt") String sortName,
            @RequestParam(defaultValue = "DESC") String sortType) {

        Sort.Direction direction;

        if (sortType.equalsIgnoreCase("ASC")) {
            direction = Sort.Direction.ASC;
        } else {
            direction = Sort.Direction.DESC;
        }

        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(direction, sortName));

        Page<UsersInfoDto> followerUsers =
                followService.getFollowerListUsers(pageable);
        return ResponseEntity.ok(followerUsers.getContent());
    }

    @CheckLogin
    @DeleteMapping("/unfollow/{followingUserId}")
    public ResponseEntity<String> unfollowUser(@PathVariable String followingUserId) {
        followService.unfollowUser(followingUserId);
        return new ResponseEntity<>("User unfollowed successfully", HttpStatus.OK);
    }

    @CheckLogin
    @GetMapping("/ListUsers/unfollowed")
    public ResponseEntity<List<UsersInfoDto>> getUnfollowedUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int pageSize,
            @RequestParam(defaultValue = "createAt") String sortName,
            @RequestParam(defaultValue = "ASC") String sortType) {

        Sort.Direction direction;

        if (sortType.equalsIgnoreCase("ASC")) {
            direction = Sort.Direction.ASC;
        } else {
            direction = Sort.Direction.DESC;
        }

        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(direction, sortName));

        Page<UsersInfoDto> followingUsers =
                followService.getUnfollowedUsers(pageable);
        return ResponseEntity.ok(followingUsers.getContent());
    }
    @CheckLogin
    @GetMapping("/ListFriends")
    public ResponseEntity<List<UsersInfoDto>> getListFriends(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int pageSize,
            @RequestParam(defaultValue = "createAt") String sortName,
            @RequestParam(defaultValue = "DESC") String sortType) {

        Sort.Direction direction;

        if (sortType.equalsIgnoreCase("ASC")) {
            direction = Sort.Direction.ASC;
        } else {
            direction = Sort.Direction.DESC;
        }

        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(direction, sortName));

        Page<UsersInfoDto> followerUsers =
                followService.getListFriends(pageable);
        return ResponseEntity.ok(followerUsers.getContent());
    }
}