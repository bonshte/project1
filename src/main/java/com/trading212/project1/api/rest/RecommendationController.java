package com.trading212.project1.api.rest;

import com.trading212.project1.api.rest.models.AdResponse;
import com.trading212.project1.api.rest.models.Mappers;
import com.trading212.project1.api.rest.models.RecommendationResponse;
import com.trading212.project1.core.RecommendationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/ad-recommendation")
public class RecommendationController {
    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @GetMapping("/{userId}/{sessionId}")
    public ResponseEntity<RecommendationResponse> getSessionRecommendations(
        @PathVariable("userId") int userId,
        @PathVariable("sessionId") int sessionId) {
        System.out.println("hit controller for ads");
        List<AdResponse> recommendations = recommendationService.getSessionRecommendedAds(userId, sessionId)
            .stream()
            .map(Mappers::fromAd)
            .toList();
        return ResponseEntity.ok(
            new RecommendationResponse(
                recommendations,
                sessionId
            )
        );
    }
}
