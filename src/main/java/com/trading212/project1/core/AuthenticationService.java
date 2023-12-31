package com.trading212.project1.core;

import com.trading212.project1.api.rest.models.AuthenticationResponse;
import com.trading212.project1.api.rest.models.input.LoginInput;
import com.trading212.project1.api.rest.models.input.RegisterInput;
import com.trading212.project1.core.exceptions.CredentialsIntegrityException;
import com.trading212.project1.core.models.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

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

        User savedUser = userService.createUser(
            registerInput.getEmail(),
            registerInput.getPassword()
        );

        String jwtToken = jwtService.generateToken(savedUser);
        return AuthenticationResponse
                .builder()
                .token(jwtToken)
                .userId(savedUser.getId())
                .build();
    }
    public AuthenticationResponse login(LoginInput loginInput) {
        if (!loginInput.isValid()) {
            throw new CredentialsIntegrityException("login data not full");
        }

        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginInput.getEmail(),
                loginInput.getPassword()
            )
        );
        User user = userService.getUser(loginInput.getEmail());
        String jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse
                .builder()
                .token(jwtToken)
                .userId(user.getId())
                .build();
    }
}
