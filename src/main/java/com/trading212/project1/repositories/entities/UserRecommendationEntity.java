package com.trading212.project1.repositories.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.units.qual.A;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRecommendationEntity {
    private int userId;
    private Long adId;
    private boolean forSale;
    private LocalDateTime recommendedAt;
}
