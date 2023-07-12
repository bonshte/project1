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
    private static final float DEFAULT_CLIENT_RATING = 2.5f;
    private static final Role DEFAULT_ROLE = Role.USER;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterInput registerInput) {
        if (!registerInput.valid()) {
            throw new CredentialsIntegrityException("registration data not full");
        }
        UserEntity savedUser = userRepository.createClient(registerInput.getUsername(),
                registerInput.getEmail(),
                registerInput.getPhoneNumber(),
                DEFAULT_ROLE,
                this.passwordEncoder.encode(registerInput.getPassword()),
                LocalDate.now(),
                DEFAULT_CLIENT_RATING);

        String jwtToken = jwtService.generateToken(savedUser);
        return AuthenticationResponse
                .builder()
                .token(jwtToken)
                .build();
    }


    public AuthenticationResponse login(LoginInput loginInput) {
        if (!loginInput.valid()) {
            throw new CredentialsIntegrityException("login data not full");
        }
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginInput.getUsername(),
                        loginInput.getPassword()
                )
        );

        var client = userRepository.getClientByUsername(loginInput.getUsername())
                .orElseThrow();
        String jwtToken = jwtService.generateToken(client);
        return AuthenticationResponse
                .builder()
                .token(jwtToken)
                .build();
    }
}