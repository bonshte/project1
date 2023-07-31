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
    private String email;
    private String password;

    public boolean isValid() {
        return email != null && password != null;
    }
}
