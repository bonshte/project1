package com.trading212.project1.repositories;

import com.trading212.project1.repositories.entities.analytical.PropertyType;

public interface AdAnalyticsRepository {

    void createAdView(int adId, String country, String subLocally, String neighbourhood, PropertyType propertyType, int price, int views);
}
