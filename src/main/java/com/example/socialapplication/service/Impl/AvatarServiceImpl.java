package com.example.socialapplication.service.Impl;

import com.example.socialapplication.model.entity.Users;
import com.example.socialapplication.repositories.UsersRepository;
import com.example.socialapplication.service.AvatarService;
import com.example.socialapplication.util.exception.NotFoundException;
import io.minio.*;
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

    String bucketName = "avatar";
    Logger logger = LoggerFactory.getLogger(AvatarServiceImpl.class);

    public AvatarServiceImpl(UsersRepository usersRepository) {
        this.minioClient = MinioClient.builder()
                .endpoint("http://localhost:9000")
                .credentials("root", "12345678")
                .build();
        this.usersRepository = usersRepository;
    }


    @Override
    public void uploadAvatar(MultipartFile file) throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();
        Users currentUser = usersRepository.findByUsername(currentUsername);

        String userId = currentUser.getId();

        try {
            // Kiểm tra bucketName
            checkBucketName(minioClient);

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
                String avatarUrl = bucketName + "/" + objectName;
                currentUser.setAvatar(avatarUrl);
                usersRepository.save(currentUser);
                logger.info("Tệp {} đã được tải lên thành công cho người dùng {}", file.getOriginalFilename(), currentUsername);
            }
        } catch (Exception e) {
            logger.error("Lỗi khi tải tệp lên MinIO", e);
            throw new Exception("Lỗi khi tải tệp lên MinIO", e);
        }
    }

    @Override
    public void deleteAvatar(String objectName) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();
        Users currentUser = usersRepository.findByUsername(currentUsername);

        String userId = currentUser.getId();

        try {
            String filepath = userId + "/" + objectName;
            // Kiểm tra xem tệp tồn tại trên MinIO hay không
            minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucketName)
                            .object(filepath)
                            .build()
            );
            // Xóa tập tin từ MinIO
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(filepath)
                            .build()
            );
            currentUser.setAvatar(null);
            usersRepository.save(currentUser);
            logger.info("Avatar deleted successfully for user: {}", currentUsername);
        } catch (MinioException | InvalidKeyException | IOException | NoSuchAlgorithmException e) {
            logger.error("Error deleting avatar for user {}: {}", currentUsername, e.getMessage());
            throw new NotFoundException("Không tìm thấy tệp đính kèm từ MinIO: " + e.getMessage());
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

    public void checkBucketName(MinioClient minioClient) throws Exception {

        BucketExistsArgs bucketExistsArgs = BucketExistsArgs.builder()
                .bucket(bucketName)
                .build();
        if (minioClient.bucketExists(bucketExistsArgs)) {
            System.out.println(bucketName + " exists.");
        } else {
            MakeBucketArgs makeBucketArgs = MakeBucketArgs.builder()
                    .bucket(bucketName)
                    .build();

            minioClient.makeBucket(makeBucketArgs);
            System.out.println(bucketName + " created.");
        }
    }
}