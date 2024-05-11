package com.example.socialapplication.service.Impl;

import com.example.socialapplication.model.entity.Medias;
import com.example.socialapplication.model.entity.Users;
import com.example.socialapplication.repositories.MediaRepository;
import com.example.socialapplication.repositories.UsersRepository;
import com.example.socialapplication.service.MediaService;
import com.example.socialapplication.util.exception.NotFoundException;
import io.minio.*;
import io.minio.errors.MinioException;
import io.minio.messages.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class MediaServiceImpl implements MediaService {
    private final MinioClient minioClient;
    private final UsersRepository usersRepository;
    private final MediaRepository mediaRepository;
    String bucketName = "posts";
    private static final Logger logger = LoggerFactory.getLogger(MediaServiceImpl.class);

    public MediaServiceImpl(UsersRepository usersRepository, MediaRepository mediaRepository) {
        this.minioClient = MinioClient.builder()
                .endpoint("http://localhost:9000")
                .credentials("root", "12345678")
                .build();
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
                assert originalFileName != null;
                String fileExtension = getFileExtension(originalFileName).toLowerCase();
                String objectType = (fileExtension.equals("mp4") || fileExtension.equals("avi") || fileExtension.equals("mov") || fileExtension.equals("wmv")) ? "videos" : "images";
                String objectName = userId + "/" + objectType + "/" + originalFileName;

                minioClient.putObject(
                        PutObjectArgs.builder()
                                .bucket(bucketName)
                                .object(objectName)
                                .stream(inputStream, inputStream.available(), -1)
                                .contentType(getContentType(originalFileName))
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

    @Override
    public Page<Medias> getAllImagesByLoggedInUser(Pageable pageable) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();
        Users currentUser = usersRepository.findByUsername(currentUsername);
        String userId = currentUser.getId();

        // Tạo đường dẫn đến thư mục chứa ảnh của người dùng
        String imagesPrefix = userId + "/images/";
        List<Medias> images = new ArrayList<>();

        try {
            // Lấy danh sách đối tượng ảnh từ MinIO
            Iterable<Result<Item>> results = minioClient.listObjects(
                    ListObjectsArgs.builder()
                            .bucket(bucketName)
                            .prefix(imagesPrefix)
                            .build()
            );

            // Lặp qua danh sách và thêm các đối tượng ảnh vào danh sách images
            for (Result<Item> result : results) {
                Item item = result.get();
                Medias media = new Medias();
                media.setBaseName(item.objectName().substring(imagesPrefix.length()));
                media.setPublicUrl(bucketName + "/" + item.objectName());
                media.setCreateAt(new Timestamp(new Date().getTime()));
                images.add(media);
            }
        } catch (Exception e) {
            logger.error("Error getting images from MinIO", e);
            // Xử lý lỗi nếu cần
        }

        // Tạo trang từ danh sách ảnh và Pageable
        return new PageImpl<>(images, pageable, images.size());
    }



    @Override
    public Page<Medias> getAllVideosByLoggedInUser(Pageable pageable) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();
        Users currentUser = usersRepository.findByUsername(currentUsername);
        String userId = currentUser.getId();

        // Tạo đường dẫn đến thư mục chứa video của người dùng
        String videosPrefix = userId + "/videos/";

        List<Medias> videos = new ArrayList<>();

        try {
            // Lấy danh sách đối tượng video từ MinIO
            Iterable<Result<Item>> results = minioClient.listObjects(
                    ListObjectsArgs.builder()
                            .bucket(bucketName)
                            .prefix(videosPrefix)
                            .build()
            );

            // Lặp qua danh sách và thêm các đối tượng video vào danh sách videos
            for (Result<Item> result : results) {
                Item item = result.get();
                Medias media = new Medias();
                media.setBaseName(item.objectName().substring(videosPrefix.length())); // Lấy tên file
                media.setPublicUrl(bucketName + "/" + item.objectName());
                media.setCreateAt(new Timestamp(new Date().getTime()));
                videos.add(media);
            }
        } catch (Exception e) {
            logger.error("Error getting videos from MinIO", e);
            // Xử lý lỗi nếu cần
        }

        // Tạo trang từ danh sách video và Pageable
        return new PageImpl<>(videos, pageable, videos.size());
    }

    @Override
    public Page<Medias> getAllImagesByUserId(String userId, Pageable pageable) {
        // Tạo đường dẫn đến thư mục chứa ảnh của người dùng
        String imagesPrefix = userId + "/images/";

        List<Medias> images = new ArrayList<>();

        try {
            // Lấy danh sách đối tượng ảnh từ MinIO
            Iterable<Result<Item>> results = minioClient.listObjects(
                    ListObjectsArgs.builder()
                            .bucket(bucketName)
                            .prefix(imagesPrefix)
                            .build()
            );

            // Lặp qua danh sách và thêm các đối tượng ảnh vào danh sách images
            for (Result<Item> result : results) {
                Item item = result.get();
                Medias media = new Medias();
                media.setBaseName(item.objectName().substring(imagesPrefix.length())); // Lấy tên file
                media.setPublicUrl(bucketName + "/" + item.objectName());
                media.setCreateAt(new Timestamp(new Date().getTime())); // Thiết lập createAt
                images.add(media);
            }
        } catch (Exception e) {
            logger.error("Error getting images from MinIO", e);
            // Xử lý lỗi nếu cần
        }

        // Tạo trang từ danh sách ảnh và Pageable
        return new PageImpl<>(images, pageable, images.size());
    }

    @Override
    public Page<Medias> getAllVideosByUserId(String userId, Pageable pageable) {
        // Tạo đường dẫn đến thư mục chứa video của người dùng
        String videosPrefix = userId + "/videos/";

        List<Medias> videos = new ArrayList<>();

        try {
            // Lấy danh sách đối tượng video từ MinIO
            Iterable<Result<Item>> results = minioClient.listObjects(
                    ListObjectsArgs.builder()
                            .bucket(bucketName)
                            .prefix(videosPrefix)
                            .build()
            );

            // Lặp qua danh sách và thêm các đối tượng video vào danh sách videos
            for (Result<Item> result : results) {
                Item item = result.get();
                Medias media = new Medias();
                media.setBaseName(item.objectName().substring(videosPrefix.length())); // Lấy tên file
                media.setPublicUrl(bucketName + "/" + item.objectName());
                media.setCreateAt(new Timestamp(new Date().getTime()));
                videos.add(media);
            }
        } catch (Exception e) {
            logger.error("Error getting videos from MinIO", e);
            // Xử lý lỗi nếu cần
        }

        // Tạo trang từ danh sách video và Pageable
        return new PageImpl<>(videos, pageable, videos.size());
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