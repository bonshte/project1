package com.trading212.project1.core.models;

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
public class AdStub {
    private Long id;
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
    private List<String> features;
    private String phoneNumber;
    private Integer yearBuilt;
    private String link;
    private String construction;
    private String description;
    private List<String> imageUrls;



    public Double calculateInBGN() {
        double bgnPrice = getPrice();
        if (currency != null) {
            bgnPrice = bgnPrice * Currency.TO_BGN.get(currency);
        }
        return bgnPrice;
    }
}