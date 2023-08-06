package com.trading212.project1.core.models;

import com.trading212.project1.core.models.scraping.AccommodationType;
import com.trading212.project1.core.models.scraping.Currency;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRequirement {
    private String town;
    private Currency currency;
    private Integer price;
    private AccommodationType accommodationType;
    private List<String> neighbourhoods;
    private List<String> features;
    private boolean forSale;


    public int priceInBGN() {
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

    public String toEmbeddableText() {
        StringBuilder text = new StringBuilder();

        text.append("Apartment in");
        if (town != null) {
            text.append(" " + town);
        }
        if (neighbourhoods != null && !neighbourhoods.isEmpty()) {
            text.append(", in neighbourhoods - " + String.join(", ", neighbourhoods));
        }
        if (accommodationType != null) {
            text.append(" apartment type " + accommodationType.toString());
        }
        if (price != null) {
            text.append(" price " + priceInBGN());
        }

        if (features != null && !features.isEmpty()) {
            text.append(", with features - " + String.join(", ", features));
        }
        return text.toString();

    }
}
