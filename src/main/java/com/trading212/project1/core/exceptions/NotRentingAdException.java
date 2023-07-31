package com.trading212.project1.core.exceptions;

public class NotRentingAdException extends RuntimeException {
    public NotRentingAdException(String msg) {
        super(msg);
    }

    public NotRentingAdException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
