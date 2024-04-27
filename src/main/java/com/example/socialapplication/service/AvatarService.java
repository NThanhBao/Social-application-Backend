package com.example.socialapplication.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public interface AvatarService {

    void uploadAvatar(MultipartFile filePath) throws Exception;

    void deleteAvatar(String objectName) throws IOException, InvalidKeyException, NoSuchAlgorithmException;

}
