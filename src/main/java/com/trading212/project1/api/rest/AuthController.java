package com.trading212.project1.api.rest;

import com.trading212.project1.api.rest.models.AuthenticationResponse;
import com.trading212.project1.api.rest.models.LoginInput;
import com.trading212.project1.api.rest.models.RegisterInput;
import com.trading212.project1.core.AuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthenticationService authenticationService;

    public AuthController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }


    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody RegisterInput registerInput
    ) {
        return ResponseEntity.ok(authenticationService.register(registerInput));
    }


    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(
            @RequestBody LoginInput loginInput
    ) {
        return ResponseEntity.ok(authenticationService.login(loginInput));

    }
}
