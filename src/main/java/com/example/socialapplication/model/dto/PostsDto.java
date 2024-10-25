package com.example.socialapplication.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PostsDto {

    private String title;

    private String body;

    private String status;

    private List<String> mediasId;

}
