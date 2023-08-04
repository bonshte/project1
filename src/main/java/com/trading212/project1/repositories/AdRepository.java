package com.trading212.project1.repositories;

import com.trading212.project1.core.models.scraping.AccommodationType;
import com.trading212.project1.core.models.scraping.Currency;
import com.trading212.project1.repositories.entities.AdEntity;

import java.util.List;

public interface AdRepository {


    AdEntity createAd(String town, String neighbourhood,
                              String district, AccommodationType accommodationType,
                              Integer price, Currency currency,
                              String propertyProvider, Integer size,
                              Integer floor, Integer totalFloors,
                              Boolean gasProvided, Boolean thermalPowerPlantProvided,
                              String phoneNumber, Integer yearBuilt,
                              String link, String construction,
                              String description, Boolean forSale, List<String> features, List<String> imageUrls);


    AdEntity getByAdId(Long adId);

    void deleteAdById(Long adId);

    List<Long> getAdIdsWithLinksIn(List<String> links);

    int deleteAdsNotIn(List<Long> adIds);
}
