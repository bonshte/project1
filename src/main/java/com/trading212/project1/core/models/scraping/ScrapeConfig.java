package com.trading212.project1.core.models.scraping;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jsoup.nodes.Document;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

@Builder
@NoArgsConstructor
@Data
@AllArgsConstructor
public class ScrapeConfig {
    private String scrapeUrl;
    private Function<AccommodationType, String> accommodationTypeCodeGenerator;
    private BiFunction<Document, String, ScrapedAd> adExtractor;
    private List<ScrapeSearchFilter> filters;

}
