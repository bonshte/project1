package com.trading212.project1.core.mappers;

import com.trading212.project1.core.models.User;
import com.trading212.project1.repositories.entities.UserEntity;

public class Mappers {
    public static User fromUserEntity(UserEntity userEntity) {
        return new User(
            userEntity.getId(),
            userEntity.getEmail(),
            userEntity.getPassword(),
            userEntity.getPremiumUntil(),
            userEntity.getCriteria(),
            userEntity.isSubscribed(),
            userEntity.getRole()
        );
    }
}
