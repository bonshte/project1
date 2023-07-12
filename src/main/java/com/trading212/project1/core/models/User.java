package com.trading212.project1.core.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDate;



@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private int id;
    private String username;
    private String phoneNumber;
    private String email;
    private LocalDate createdAt;
    private float rating;
    private Role role;
}
