package com.trading212.project1.core.exceptions;

public class ScrapeFormatMissMatchException extends RuntimeException {
    public ScrapeFormatMissMatchException(String msg) {
        super(msg);
    }

    public ScrapeFormatMissMatchException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
