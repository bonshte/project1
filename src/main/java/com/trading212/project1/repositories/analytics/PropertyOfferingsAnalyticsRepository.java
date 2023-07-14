package com.trading212.project1.repositories.analytics;

import com.trading212.project1.core.models.PropertyType;

public interface PropertyOfferingsAnalyticsRepository {

    void createPropertyOffering(String country, String subLocally, String neighbourhood, int propertyArea, PropertyType propertyType, int price);
}
