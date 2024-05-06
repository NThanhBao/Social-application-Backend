package com.example.socialapplication.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
public class FavoritesDto {

    private String postsID;

    private String title;

    private String body;

    private String status;

    private String totalLike;

    private List<String> mediasId;

    private String totalComment;

    private String userID;

    private Timestamp createAt;

}