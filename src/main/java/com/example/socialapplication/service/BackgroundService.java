package com.example.socialapplication.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public interface BackgroundService {

    void uploadBackground(MultipartFile filePath) throws Exception;

    void deleteBackground(String objectName) throws IOException, InvalidKeyException, NoSuchAlgorithmException;

}
