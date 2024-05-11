package com.example.socialapplication.model.dto;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class UsersDto {

    private String username;

    private String password;

    private String mail;

    private String phoneNumber;

    private String firstName;

    private String lastName;

    private boolean gender;

    private Timestamp dateOfBirth;

    private String address;

    private String avatar;

    private String background;

}
