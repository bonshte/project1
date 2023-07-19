package com.trading212.project1.core;

import com.trading212.project1.api.rest.models.AuthenticationResponse;
import com.trading212.project1.api.rest.models.LoginInput;
import com.trading212.project1.api.rest.models.RegisterInput;
import com.trading212.project1.core.exceptions.CredentialsIntegrityException;
import com.trading212.project1.repositories.entities.UserEntity;
import com.trading212.project1.core.models.Role;
import com.trading212.project1.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterInput registerInput) {
        if (!registerInput.isValid()) {
            throw new CredentialsIntegrityException("registration data not full");
        }
        UserEntity savedUser = userService.createUser(
            registerInput.getUsername(),
            registerInput.getEmail(),
            registerInput.getPhoneNumber(),
            registerInput.getPassword());

        String jwtToken = jwtService.generateToken(savedUser);
        return AuthenticationResponse
                .builder()
                .token(jwtToken)
                .build();
    }


    public AuthenticationResponse login(LoginInput loginInput) {
        if (!loginInput.isValid()) {
            throw new CredentialsIntegrityException("login data not full");
        }
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginInput.getUsername(),
                        loginInput.getPassword()
                )
        );

        UserEntity user = userService.getUser(loginInput.getUsername());
        String jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse
                .builder()
                .token(jwtToken)
                .build();
    }
}
