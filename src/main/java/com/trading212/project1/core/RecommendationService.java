package com.trading212.project1.core;

import com.trading212.project1.core.exceptions.UnauthorizedException;
import com.trading212.project1.core.mappers.Mappers;
import com.trading212.project1.core.models.Ad;
import com.trading212.project1.core.models.ChatSession;
import com.trading212.project1.core.models.ChatSessionRecommendation;
import com.trading212.project1.core.models.User;
import com.trading212.project1.repositories.RecommendationRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RecommendationService {
    private final RecommendationRepository recommendationRepository;
    private final AdService adService;

    private final AuthorizationService authorizationService;

    public RecommendationService(RecommendationRepository recommendationRepository, AdService adService,
                                 AuthorizationService authorizationService) {
        this.recommendationRepository = recommendationRepository;
        this.adService = adService;
        this.authorizationService = authorizationService;
    }

    public List<Ad> getSessionRecommendedAds(int userId, int sessionId) {
        if (authorizationService.isAuthorizedUserSessionRequest(userId, sessionId)) {
            throw new UnauthorizedException("unauthorized access to " + userId + " session " + sessionId);
        }

        List<ChatSessionRecommendation> recommendations =
            recommendationRepository.getRecommendationsForSession(sessionId)
                .stream()
                .map(Mappers::fromChatSessionRecommendationEntity)
                .toList();

        Map<Long, LocalDateTime> adIdToRecommendedAt = recommendations.stream()
            .collect(Collectors.toMap(ChatSessionRecommendation::getAdId, ChatSessionRecommendation::getRecommendedAt));

        List<Ad> ads = adService.getAds(new ArrayList<>(adIdToRecommendedAt.keySet()));

        List<Ad> sortedAds = new ArrayList<>(ads);

        sortedAds.sort((ad1, ad2) ->
            adIdToRecommendedAt.get(ad2.getAdId()).compareTo(adIdToRecommendedAt.get(ad1.getAdId())));

        return sortedAds;
    }

    @Transactional
    public void createRecommendations(List<Ad> recommendedAds, int userId, int sessionId) {
        LocalDateTime timestamp = LocalDateTime.now();
        for (var recommendedAD : recommendedAds) {
            recommendationRepository.createRecommendationForSession(
                recommendedAD.getAdId(),
                sessionId,
                timestamp,
                userId,
                recommendedAD.getForSale()
            );
        }
    }
}
