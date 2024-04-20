package com.example.socialapplication.model.dto;


import lombok.Data;

import java.sql.Timestamp;

@Data
public class SearchUserDto {
    //chỉ in những trường này khi tìm kiếm ----
    private String id;
    private String mail;
    private String phoneNumber;
    private String firstName;
    private String lastName;
    private boolean gender;
    private Timestamp dateOfBirth;
    private String address;
    private String avatar;

}
