package com.trading212.project1.core.models.scraping;

import com.trading212.project1.core.models.AdStub;
import com.trading212.project1.core.models.SearchFilter;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class ScrapingResult {
    private List<AdStub> scrapedAds;
    private List<SearchFilter> failedFilters;
}
