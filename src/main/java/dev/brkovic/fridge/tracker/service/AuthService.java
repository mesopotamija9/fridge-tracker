package dev.brkovic.fridge.tracker.service;

import dev.brkovic.fridge.api.model.*;

public interface AuthService {
    AuthResponse register(RegisterRequest registerRequest);
    AuthResponse login(LoginRequest loginRequest);
    TokenResponse refreshToken(RefreshTokenRequest refreshTokenRequest);
    void removeExpiredRefreshTokens();
}
