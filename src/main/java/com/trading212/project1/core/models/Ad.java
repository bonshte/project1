package com.trading212.project1.core.models;

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
public class Ad {
    private Long adId;
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
    private Boolean gasProvided;
    private Boolean thermalPowerPlantProvided;
    private Boolean forSale;
    private List<String> features;
    private String phoneNumber;
    private Integer yearBuilt;
    private String link;
    private String construction;
    private String description;
    private List<String> imageUrls;

    public int calculateInBGN() {
        double priceInBGN;
        switch (currency) {
            case BGN:
                priceInBGN = price;
                break;
            case USD:
                priceInBGN = price * 1.8;
                break;
            case EURO:
                priceInBGN = price * 1.95;
                break;
            default:
                priceInBGN = price;
                break;
        }
        return (int) Math.round(priceInBGN);
    }

    public String getSummary() {
        return "Apartment in" +
            (district != null ? " district " + district : "") +
            (town != null ? " " + town + "," : "") +
            (neighbourhood != null ? " neighbourhood - " + neighbourhood + "," : "") +
            (accommodationType != null ? " apartment type - " + accommodationType.toDescriptionString() + "," : "") +
            (price != null ? " price: " + calculateInBGN() + "," : "") +
            (propertyProvider != null ? " " + propertyProvider + "," : "") +
            (size != null ? " square meters " + size + "," : "") +
            (gasProvided ? " has gas heating," : "") +
            (thermalPowerPlantProvided ? " has thermal power plant heating," : "") +
            (features != null ? " features - " + Strings.join( " ", features) + "," : "") +
            (description != null ? " " + description : "");
    }


}
