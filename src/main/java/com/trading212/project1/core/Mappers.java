package com.trading212.project1.core;

import com.trading212.project1.core.models.User;
import com.trading212.project1.repositories.entities.UserEntity;

public class Mappers {
    public static User fromUserEntity(UserEntity userEntity) {
        return new User(userEntity.getId(),
                userEntity.getUsername(),
                userEntity.getPhoneNumber(),
                userEntity.getEmail(),
                userEntity.getCreatedAt(),
                userEntity.getRating(),
                userEntity.getRole());
    }
}
