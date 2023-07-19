package com.trading212.project1.core;

import com.trading212.project1.core.models.Role;
import com.trading212.project1.repositories.UserRepository;
import com.trading212.project1.repositories.entities.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class UserService {
    private static final float DEFAULT_CLIENT_RATING = 2.5f;
    private static final Role DEFAULT_ROLE = Role.USER;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    public UserEntity createUser(String username, String email,
                                 String phoneNumber, String password) {
        return userRepository.createUser(username,
            email,
            phoneNumber,
            DEFAULT_ROLE,
            this.passwordEncoder.encode(password),
            LocalDate.now(),
            DEFAULT_CLIENT_RATING);
    }


    public UserEntity getUser(String username) {
        return userRepository.getUser(username)
            .orElseThrow();
    }
}
