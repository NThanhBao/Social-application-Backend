package com.example.socialapplication.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class MessengerDto {

   private Long id;

   private String sender;

   private String content;

   private String recipient;

   private Timestamp createdAt;

}
