package com.example.socialapplication.service;

import org.springframework.web.multipart.MultipartFile;

public interface MediaService {
    void uploadMedia(MultipartFile filePath) throws Exception;
    void deletePost(String objectName) throws Exception;
}