package com.trading212.project1.repositories;

import com.trading212.project1.core.models.Role;
import com.trading212.project1.repositories.entities.UserEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface UserRepository {

    Optional<UserEntity> getUser(String username);

    List<UserEntity> getUsers();

    UserEntity createUser(String username, String email, String phoneNumber,
                          Role role, String password, LocalDate dateCreated, float rating);

    int deleteUser(String username);


}
