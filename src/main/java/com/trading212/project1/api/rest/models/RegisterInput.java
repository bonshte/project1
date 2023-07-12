package com.trading212.project1.api.rest.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterInput {
    private String username;
    private String email;
    private String phoneNumber;
    private String password;

    public boolean valid() {
        return username != null &&
                email != null &&
                phoneNumber != null &&
                password != null;
    }
}
