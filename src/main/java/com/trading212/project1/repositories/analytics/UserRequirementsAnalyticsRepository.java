package com.trading212.project1.repositories.analytics;

import com.trading212.project1.core.models.PropertyType;

public interface UserRequirementsAnalyticsRepository {
    void createUserRequirement(String country, String subLocally, String neighbourhood, PropertyType propertyType, int price);
}
