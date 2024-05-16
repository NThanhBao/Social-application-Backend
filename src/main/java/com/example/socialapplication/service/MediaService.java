package com.example.socialapplication.service;

import com.example.socialapplication.model.entity.Medias;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface MediaService {

    String uploadMedia(MultipartFile filePath) throws Exception;

    void deletePost(String objectName) throws Exception;

    Page<Medias> getAllImagesByLoggedInUser(Pageable pageable);

    Page<Medias> getAllVideosByLoggedInUser(Pageable pageable);

    Page<Medias> getAllImagesByUserId(String userId, Pageable pageable);

    Page<Medias> getAllVideosByUserId(String userId, Pageable pageable);
}