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
    private static final String NEIGHBOURHOOD_FIELD = "neighbourhood";
    private static final String PRICE_FIELD = "price";
    private static final String ACCOMMODATION_TYPE_FIELD = "accommodation_type";
    private String townField;
    private List<String> neighbourhoods;
    private Integer priceField;
    private String accommodationTypeField;

    public String toFilterExpression() {
        StringBuilder expression = new StringBuilder();
        if (townField != null) {
            expression.append(TOWN_FIELD).append(" == '").append(townField).append("' && ");
        }
        if (neighbourhoods != null && !neighbourhoods.isEmpty()) {
            expression.append(NEIGHBOURHOOD_FIELD).append(" in (");
            for (String neighbourhood : neighbourhoods) {
                expression.append("'").append(neighbourhood).append("',");
            }
            expression.deleteCharAt(expression.length() - 1); // Remove last comma
            expression.append(") && ");
        }
        if (priceField != null) {
            int lowerBound = (int) Math.round(priceField * 0.95);
            int upperBound = (int) Math.round(priceField * 1.05);
            expression.append(PRICE_FIELD).append(" >= ").append(lowerBound)
                .append(" && ").append(PRICE_FIELD).append(" <= ").append(upperBound).append(" && ");
        }
        if (accommodationTypeField != null) {
            expression.append(ACCOMMODATION_TYPE_FIELD).append(" == '").append(accommodationTypeField).append("' && ");
        }

        // Remove trailing '&& '
        if (expression.length() > 0) {
            expression.setLength(expression.length() - 3);
        }

        return expression.toString();
    }
}
