package com.example.socialapplication.service.Impl;

import com.example.socialapplication.model.entity.Medias;
import com.example.socialapplication.model.entity.Users;
import com.example.socialapplication.repositories.MediaRepository;
import com.example.socialapplication.repositories.UsersRepository;
import com.example.socialapplication.service.MediaService;
import com.example.socialapplication.util.exception.NotFoundException;
import io.minio.*;
import io.minio.errors.MinioException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

@Service
public class MediaServiceImpl implements MediaService {
    private final MinioClient minioClient;
    private final UsersRepository usersRepository;
    private final MediaRepository mediaRepository;
    String bucketName = "posts";
    private static final Logger logger = LoggerFactory.getLogger(MediaServiceImpl.class);

    public MediaServiceImpl(MinioClient minioClient, UsersRepository usersRepository, MediaRepository mediaRepository) {
        this.minioClient = minioClient;
        this.usersRepository = usersRepository;
        this.mediaRepository = mediaRepository;
    }

    @Override
    public String uploadMedia(MultipartFile filePath) throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();
        Users currentUser = usersRepository.findByUsername(currentUsername);
        String userId = currentUser.getId();

        try {
            // Kiểm tra bucketName
            checkBucketName(minioClient);

            try (InputStream inputStream = new BufferedInputStream(filePath.getInputStream())) {
                String originalFileName = filePath.getOriginalFilename();
                String objectName = userId + "/" + originalFileName;
                minioClient.putObject(
                        PutObjectArgs.builder()
                                .bucket(bucketName)
                                .object(objectName)
                                .stream(inputStream, inputStream.available(), -1)
                                .contentType(getContentType(objectName))
                                .build()
                );

                Medias medias = new Medias();
                String mediaId = UUID.randomUUID().toString();
                medias.setId(mediaId);
                medias.setBaseName(filePath.getOriginalFilename());
                String setBaseName = bucketName + "/" + objectName;
                medias.setPublicUrl(setBaseName);

                mediaRepository.save(medias);

                logger.info("File {} uploaded successfully to MinIO for user {}", originalFileName, currentUsername);

                // Trả về ID của media
                return mediaId;
            }
        } catch (Exception e) {
            logger.error("Error uploading file to MinIO", e);
            throw new Exception("Error uploading file to MinIO", e);
        }
    }


    @Override
    public void deletePost(String objectName) {
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

            // Nếu tệp tồn tại, thực hiện xóa
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(filepath)
                            .build()
            );

            logger.info("File {} deleted successfully from MinIO for user {}", objectName, currentUsername);
        } catch (MinioException | InvalidKeyException | IOException | NoSuchAlgorithmException e) {
            logger.error("Error deleting file from MinIO", e);
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
            case "mp4" -> "video/mp4";
            case "avi" -> "video/x-msvideo";
            case "mov" -> "video/quicktime";
            case "wmv" -> "video/x-ms-wmv";
            default -> "application/octet-stream";
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