package com.trading212.project1.core.models.scraping;

import com.trading212.project1.core.models.scraping.AccommodationType;
import com.trading212.project1.core.models.scraping.Currency;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ScrapedAd {
    private String town;
    private String neighbourhood;
    private String district;
    private AccommodationType accommodationType;
    private Integer price;
    private Currency currency;
    private String propertyProvider;
    private Integer size;
    private Integer floor;
    private Integer totalFloors;
    private boolean gasProvided;
    private boolean thermalPowerPlantProvided;
    private String phoneNumber;
    private Integer yearBuilt;
    private String link;
    private String construction;
    private String description;
    private List<String> features;
    private List<String> imageUrls;
}