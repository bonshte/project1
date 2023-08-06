package com.trading212.project1.repositories;

import com.trading212.project1.repositories.entities.ChatSessionRecommendationEntity;

import java.time.LocalDateTime;
import java.util.List;

public interface RecommendationRepository {
    List<ChatSessionRecommendationEntity> getRecommendationsForSession(int sessionId);

    ChatSessionRecommendationEntity createRecommendationForSession(Long adId, int sessionId,
                                                                   LocalDateTime timestamp, int userId, boolean forSale);


    List<ChatSessionRecommendationEntity> getRecommendationsForUser(int userId);
}
