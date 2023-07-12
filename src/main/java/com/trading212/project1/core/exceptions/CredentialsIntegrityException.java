package com.trading212.project1.core.exceptions;

public class CredentialsIntegrityException extends RuntimeException {
    public CredentialsIntegrityException(String msg) {
        super(msg);
    }

    public CredentialsIntegrityException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
