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
public class ChatSessionRecommendationEntity {
    private int chatSessionId;
    private Long adId;
    private int userId;
    private LocalDateTime recommendedAt;
    private boolean forSale;
}
