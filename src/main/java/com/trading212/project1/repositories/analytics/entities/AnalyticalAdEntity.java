package com.trading212.project1.repositories.analytics.entities;

import com.trading212.project1.core.models.PropertyType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticalAdEntity {
    private String country;
    private String locally;
    private String neighbourhood;
    private PropertyType propertyType;
    private int price;
}
