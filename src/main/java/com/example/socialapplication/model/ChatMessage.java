package com.example.socialapplication.model;

import com.example.socialapplication.model.entity.Enum.MessageType;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessage {

    private MessageType type;

    private String content;

    private String sender;

}
