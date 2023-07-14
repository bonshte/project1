package com.trading212.project1.repositories;

import com.trading212.project1.repositories.entities.analytical.PropertyType;

public interface PropertyOfferingsAnalyticsRepository {

    void createPropertyOffering(String country, String subLocally, String neighbourhood, int propertyArea, PropertyType propertyType, int price);
}
