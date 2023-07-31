package com.trading212.project1.repositories;

import com.trading212.project1.core.models.Role;
import com.trading212.project1.repositories.entities.UserEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface UserRepository {

    Optional<UserEntity> getUser(String email);

    List<UserEntity> getUsers();

    UserEntity createUser(String email,
                          Role role, String password);

    int deleteUser(int userId);

    void setSubscription(int userId, boolean subscription);

    void setPremiumUser(int userId, LocalDate until);
}
