package com.trading212.project1.core.models.scraping;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ScrapeSearchFilter {
    private Location location;
    private AccommodationType accommodationType;

}