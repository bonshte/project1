package com.trading212.project1.repositories;

import com.trading212.project1.core.models.Role;
import com.trading212.project1.repositories.entities.UserEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface UserRepository {

    Optional<UserEntity> getClientByUsername(String username);

    List<UserEntity> getClients();

    public UserEntity createClient(String username, String email, String phoneNumber,
                                   Role role, String password, LocalDate dateCreated, float rating);

    int deleteClientByUsername(String username);


}
