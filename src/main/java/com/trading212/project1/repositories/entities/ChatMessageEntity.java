package com.trading212.project1.repositories.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageEntity {
    private int messageId;
    private int chatSessionId;
    private String sentMessage;
    private String translatedMessage;
    private boolean fromUser;
    private LocalDateTime timestamp;
    private boolean adsFound;
}
