package com.trading212.project1.repositories.analytics;

import com.trading212.project1.core.models.PropertyType;

public interface AdAnalyticsRepository {

    void createAdView(int adId, String country, String subLocally, String neighbourhood, PropertyType propertyType, int price, int views);
}
