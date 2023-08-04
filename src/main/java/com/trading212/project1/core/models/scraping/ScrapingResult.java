package com.trading212.project1.core.models.scraping;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class ScrapingResult {
    private List<ScrapedAd> scrapedAds;
    private List<ScrapeSearchFilter> failedFilters;
}
