package com.trading212.project1.repositories;

import com.trading212.project1.repositories.entities.analytical.PropertyType;

public interface UserRequirementsAnalyticsRepository {
    void createUserRequirement(String country, String subLocally, String neighbourhood, PropertyType propertyType, int price);
}
