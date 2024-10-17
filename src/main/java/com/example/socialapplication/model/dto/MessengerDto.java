package com.example.socialapplication.model.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;

@Component
@Getter
@Setter
public class MessengerDto {

   private Long id;

   private String sender;

   private String content;

   private String recipient;

   private Timestamp createdAt;
}
