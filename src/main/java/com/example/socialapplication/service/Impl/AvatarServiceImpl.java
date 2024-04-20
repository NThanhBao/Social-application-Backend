package com.example.socialapplication.service.Impl;

import com.example.socialapplication.model.entity.Users;
import com.example.socialapplication.repositories.UsersRepository;
import com.example.socialapplication.service.AvatarService;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.errors.MinioException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
public class AvatarServiceImpl implements AvatarService {

    private final MinioClient minioClient;
    private final UsersRepository usersRepository;
    Logger logger = LoggerFactory.getLogger(AvatarServiceImpl.class);
    public AvatarServiceImpl(MinioClient minioClient, UsersRepository usersRepository) {
        this.minioClient = minioClient;
        this.usersRepository = usersRepository;
    }


    @Override
    public void uploadAvatar(MultipartFile file) throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();
        Users currentUser = usersRepository.findByUsername(currentUsername);

        String userId = currentUser.getId();

        String bucketName = "avatar";

        try (InputStream inputStream = new BufferedInputStream(file.getInputStream())) {
            String objectName = userId + "/" + file.getOriginalFilename();
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .stream(inputStream, inputStream.available(), -1)
                            .contentType(getContentType(objectName))
                            .build()
            );
            String avatarUrl = "http://localhost:9000/" + bucketName + "/" + objectName;
            currentUser.setAvatar(avatarUrl);
            usersRepository.save(currentUser);
            logger.info("File {} uploaded successfully for user {}", file.getOriginalFilename(), currentUsername);
        } catch (Exception e) {
            logger.error("Error uploading file to MinIO", e);
            throw new Exception("Error uploading file to MinIO", e);
        }
    }
    @Override
    public void deleteAvatar(String objectName) throws IOException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();
        Users currentUser = usersRepository.findByUsername(currentUsername);

        String userId = currentUser.getId();

        String bucketName = "avatar";
        try {
            String objectFullName = userId + "/" + objectName;

            // Xóa tập tin từ MinIO
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectFullName)
                            .build()
            );

            // Cập nhật cơ sở dữ liệu
            // Đặt đường dẫn avatar của người dùng thành null
            currentUser.setAvatar(null);
            // Lưu thay đổi vào cơ sở dữ liệu
            usersRepository.save(currentUser);

            logger.info("File {} deleted successfully for user {}", objectName, currentUsername);
        } catch (MinioException e) {
            logger.error("Failed to delete file: " + e.getMessage(), e);
            throw new IOException("Failed to delete file: " + e.getMessage());
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            logger.error("An unexpected error occurred: " + e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
    private String getContentType(String fileName) {
        String fileExtension = getFileExtension(fileName).toLowerCase();
        return switch (fileExtension) {
            case "jpg", "jpeg" -> "image/jpeg";
            case "png" -> "image/png";
            case "gif" -> "image/gif";
            case "bmp" -> "image/bmp";
            default -> "application/octet-stream"; // Kiểu MIME mặc định
        };
    }
    private String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        return (lastDotIndex == -1) ? "" : fileName.substring(lastDotIndex + 1);
    }
}