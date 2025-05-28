package dev.brkovic.fridge.tracker.api;

import dev.brkovic.fridge.api.AuthApi;
import dev.brkovic.fridge.api.model.*;
import dev.brkovic.fridge.tracker.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthApiImpl implements AuthApi {

    private final AuthService authService;

    @Override
    public ResponseEntity<AuthResponse> register(RegisterRequest registerRequest) {
        log.info("Received register request: {}", registerRequest);
        AuthResponse response = authService.register(registerRequest);

        log.info("Successful user registration. Returning response: {}", response);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    public ResponseEntity<Void> apiV1AuthLogoutPost() {
        log.info("Received logout request");
        authService.logout();

        log.info("Successful logged out user");
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<AuthResponse> login(LoginRequest loginRequest) {
        log.info("Received login request: {}", loginRequest);
        AuthResponse response = authService.login(loginRequest);

        log.info("Successful user login. Returning response: {}", response);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<TokenResponse> refreshToken(RefreshTokenRequest refreshTokenRequest) {
        log.info("Received refresh token request: {}", refreshTokenRequest);
        TokenResponse response = authService.refreshToken(refreshTokenRequest);

        log.info("Successfully refreshed token. Returning response: {}", response);
        return ResponseEntity.ok(response);
    }
}
