package com.trading212.project1.repositories.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatSessionEntity {
    private int sessionId;
    private String description;
    private int userId;
}
