package com.trading212.project1.repositories;

import com.trading212.project1.repositories.entities.ChatSessionRecommendationEntity;
import com.trading212.project1.repositories.entities.UserRecommendationEntity;

import java.util.List;

public interface RecommendationRepository {
    List<ChatSessionRecommendationEntity> getRecommendationsForSession(int sessionId);

    void createRecommendationsForSession(List<Long> adId);

    void createRecommendationsForUser(List<Long> adId);

    List<UserRecommendationEntity> getRecommendationsForUser(int userId);
}
