package com.example.socialapplication.model.dto;

import lombok.*;

import java.sql.Timestamp;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UsersInfoDto {

    private String id;

    private String username;

    private String firstName;

    private String lastName;

    private String role;

    private boolean gender;

    private String phoneNumber;

    private Timestamp dateOfBirth;

    private String mail;

    private String address;

    private String avatar;

    private String background;
}
