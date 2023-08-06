package com.trading212.project1.core.models.scraping;

import java.util.Map;

public enum Currency {
    BGN,
    USD,
    EURO;

    public static final Map<Currency, Double> TO_BGN = Map.of(
            EURO, 1.95,
            USD, 1.8,
            BGN, 1.0
    );

    public static Currency fromGPTFunctionArgument(String argument) {
        switch (argument) {
            case "bgn": return Currency.BGN;
            case "euro": return Currency.EURO;
            case "usd": return Currency.USD;
            default:
                throw new RuntimeException("invalid gpt function call argument for currency");
        }
    }
}