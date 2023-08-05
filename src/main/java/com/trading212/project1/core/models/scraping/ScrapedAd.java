package com.trading212.project1.core.models.scraping;

import com.beust.jcommander.Strings;
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

    public String toEmbeddableText() {
        return "Apartment in" +
            (district != null ? " district " + district : "") +
            (town != null ? " " + town + "," : "") +
            (neighbourhood != null ? " neighbourhood " + neighbourhood + "," : "") +
            (accommodationType != null ? " apartment type " + accommodationType.toDescriptionString() + "," : "") +
            (price != null ? " price " + calculateInBGN().intValue() + "," : "") +
            (propertyProvider != null ? " " + propertyProvider + "," : "") +
            (size != null ? " square meters " + size + "," : "") +
            (isGasProvided() ? " has gas heating," : "") +
            (isThermalPowerPlantProvided() ? " has thermal power plant heating," : "") +
            (features != null ? " features " + Strings.join( " ", features) + "," : "") +
            (description != null ? " " + description : "");
    }

    public Double calculateInBGN() {
        double bgnPrice = getPrice();
        if (currency != null) {
            bgnPrice = bgnPrice * Currency.TO_BGN.get(currency);
        }
        return bgnPrice;
    }
}