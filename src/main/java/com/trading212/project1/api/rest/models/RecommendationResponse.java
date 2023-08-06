package com.trading212.project1.api.rest.models;

import com.trading212.project1.core.models.Ad;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecommendationResponse {
    private List<AdResponse> recommendedAds;
    private int chatSessionId;
}
