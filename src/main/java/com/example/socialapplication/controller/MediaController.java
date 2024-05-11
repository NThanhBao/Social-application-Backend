package com.example.socialapplication.controller;

import com.example.socialapplication.model.entity.Medias;
import com.example.socialapplication.service.MediaService;
import com.example.socialapplication.util.annotation.CheckLogin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/media")
public class MediaController {
    private final MediaService mediaService;

    public MediaController(MediaService mediaService) {
        this.mediaService = mediaService;
    }

    @CheckLogin
    @GetMapping("/images")
    public ResponseEntity<List<Medias>> getImages(@RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "10") int pageSize,
                                                  @RequestParam(defaultValue = "createAt") String sortName,
                                                  @RequestParam(defaultValue = "DESC") String sortType) {
        try {
            Sort.Direction direction = sortType.equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC;
            Page<Medias> images = mediaService.getAllImagesByLoggedInUser(PageRequest.of(page, pageSize, Sort.by(direction, sortName)));
            return ResponseEntity.ok(images.getContent());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @CheckLogin
    @GetMapping("/videos")
    public ResponseEntity<List<Medias>> getVideos(@RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "10") int pageSize,
                                                  @RequestParam(defaultValue = "createAt") String sortName,
                                                  @RequestParam(defaultValue = "DESC") String sortType) {
        try {
            Sort.Direction direction = sortType.equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC;
            Page<Medias> images = mediaService.getAllVideosByLoggedInUser(PageRequest.of(page, pageSize, Sort.by(direction, sortName)));
            return ResponseEntity.ok(images.getContent());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/user/{userId}/images")
    public ResponseEntity<List<Medias>> getUserImages(@PathVariable String userId,
                                                      @RequestParam(defaultValue = "0") int page,
                                                      @RequestParam(defaultValue = "10") int pageSize,
                                                      @RequestParam(defaultValue = "createAt") String sortName,
                                                      @RequestParam(defaultValue = "DESC") String sortType) {
        try {
            Sort.Direction direction = sortType.equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC;
            Page<Medias> images = mediaService.getAllImagesByUserId(userId,PageRequest.of(page, pageSize, Sort.by(direction, sortName)));
            return ResponseEntity.ok(images.getContent());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/user/{userId}/videos")
    public ResponseEntity<List<Medias>> getUserVideos(@PathVariable String userId,
                                                      @RequestParam(defaultValue = "0") int page,
                                                      @RequestParam(defaultValue = "10") int pageSize,
                                                      @RequestParam(defaultValue = "createAt") String sortName,
                                                      @RequestParam(defaultValue = "DESC") String sortType) {
        try {
            Sort.Direction direction = sortType.equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC;
            Page<Medias> images = mediaService.getAllVideosByUserId(userId,PageRequest.of(page, pageSize, Sort.by(direction, sortName)));
            return ResponseEntity.ok(images.getContent());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}