package com.example.socialapplication.model.dto;

import com.example.socialapplication.model.entity.Enum.EnableType;
import com.example.socialapplication.model.entity.Enum.RoleType;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class UsersDto {

    private String id;

    private String username;

    private String password;

    private String mail;

    private String phoneNumber;

    private String firstName;

    private String lastName;

    private EnableType enableType;

    private RoleType roleType;

    private boolean gender;

    private Timestamp dateOfBirth;

    private String address;

    private String avatar;

    private String background;

}
