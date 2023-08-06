package com.trading212.project1.core.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SimilaritySearchFilter {
    private static final String TOWN_FIELD = "town";
    private static final String PRICE_FIELD = "price";
    private static final String ACCOMMODATION_TYPE_FIELD = "accommodation_type";
    private String townField;
    private Integer priceField;
    private String accommodationTypeField;

    public String toFilterExpression() {
        StringBuilder expression = new StringBuilder();
        if (townField != null) {
            String formattedTownField = townField.toLowerCase().replaceAll("^(town of|city of)\\s*", "");
            expression.append(TOWN_FIELD).append(" == '").append(formattedTownField).append("' && ");
        }

        if (priceField != null) {
            int lowerBound = (int) Math.round(priceField * 0.80);
            int upperBound = (int) Math.round(priceField * 1.20);
            expression.append(PRICE_FIELD).append(" >= ").append(lowerBound)
                .append(" && ").append(PRICE_FIELD).append(" <= ").append(upperBound).append(" && ");
        }
        if (accommodationTypeField != null) {
            expression.append(ACCOMMODATION_TYPE_FIELD).append(" == '").append(accommodationTypeField).append("' && ");
        }

        if (expression.length() > 0) {
            expression.setLength(expression.length() - 3);
        }
        System.out.println("this is the expression to search with");
        System.out.println(expression);
        return expression.toString();
    }

}
