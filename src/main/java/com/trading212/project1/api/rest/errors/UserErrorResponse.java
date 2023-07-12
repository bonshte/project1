package com.trading212.project1.api.rest.errors;

import lombok.Data;

@Data
public class UserErrorResponse {
    private String description;
    private int status;
    private long timestamp;
}
