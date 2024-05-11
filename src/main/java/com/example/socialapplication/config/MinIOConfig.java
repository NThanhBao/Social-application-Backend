//package com.example.socialapplication.config;
//
//import io.minio.MinioClient;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//@Configuration
//public class MinIOConfig {
//
//    private static final Logger logger = LoggerFactory.getLogger(MinIOConfig.class);
//
//    @Value("${minio.endpoint}")
//    private String endpoint;
//
//    @Value("${minio.accessKey}")
//    private String accessKey;
//
//    @Value("${minio.secretKey}")
//    private String secretKey;
//
//    @Bean
//    public MinioClient minioClient() {
//        logger.info("Endpoint: {}", endpoint);
//        logger.info("Access Key: {}", accessKey);
//        logger.info("Secret Key: {}", secretKey);
//
//        return MinioClient.builder()
//                .endpoint(endpoint)
//                .credentials(accessKey, secretKey)
//                .build();
//    }
//}