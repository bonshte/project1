package com.trading212.project1.core.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessage {
    private int chatSessionId;
    private String sentMessage;
    private String translatedMessage;
    private boolean fromUser;
    private LocalDateTime timeSent;
    private boolean adsFound;
}
