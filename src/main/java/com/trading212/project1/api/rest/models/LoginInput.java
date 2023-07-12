package com.trading212.project1.api.rest.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginInput {
    private String username;
    private String password;

    public boolean valid() {
        return username != null && password != null;
    }
}
