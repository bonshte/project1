package com.trading212.project1.core;

import com.trading212.project1.core.models.Role;
import com.trading212.project1.core.models.User;
import com.trading212.project1.core.mappers.Mappers;
import com.trading212.project1.repositories.UserRepository;
import com.trading212.project1.repositories.entities.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private static final Role DEFAULT_ROLE = Role.USER;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    public User createUser(String email, String password) {
        UserEntity userEntity = userRepository.createUser(
            email,
            DEFAULT_ROLE,
            this.passwordEncoder.encode(password)
        );
        return Mappers.fromUserEntity(userEntity);
    }

    public User getUser(String email) {
        UserEntity userEntity =  userRepository.getUser(email)
            .orElseThrow();
        return Mappers.fromUserEntity(userEntity);
    }


}
