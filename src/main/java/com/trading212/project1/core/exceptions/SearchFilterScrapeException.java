package com.trading212.project1.core.exceptions;

public class SearchFilterScrapeException extends RuntimeException {
    public SearchFilterScrapeException(String msg) {
        super(msg);
    }

    public SearchFilterScrapeException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
