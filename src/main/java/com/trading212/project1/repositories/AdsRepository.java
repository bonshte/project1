package com.trading212.project1.repositories;

import com.trading212.project1.repositories.entities.AdEntity;
import com.trading212.project1.repositories.entities.AdFeatureEntity;
import com.trading212.project1.repositories.entities.AdImageEntity;

import java.util.List;
import java.util.Optional;

public interface AdsRepository {

    AdEntity createAd(AdEntity adEntity);

    Optional<AdEntity> getAdById(int adId);

    int deleteAd(int adId);

    List<AdImageEntity> getImagesForAd(int adId);

    List<AdFeatureEntity> getFeaturesForAd(int adId);

    int createImagesForAd(int adId, List<String> images);

    int createFeaturesForAd(int adId, List<String> features);

    List<AdEntity> getAdsByLinks(List<String> links);

    int deleteAdsNotInLinks(List<String> links);
}
