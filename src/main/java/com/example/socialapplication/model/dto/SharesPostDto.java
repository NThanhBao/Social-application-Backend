package com.example.socialapplication.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class SharesPostDto {
    private String postId;
    private String createBy;
    private Timestamp createAt;
}
