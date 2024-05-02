package com.example.socialapplication.model.dto;

import com.example.socialapplication.model.entity.Posts;
import com.example.socialapplication.model.entity.Users;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class SavedPostDto {
    private String id;
    private Posts postId;
    private Users createBy;
    private Timestamp createAt;
}
