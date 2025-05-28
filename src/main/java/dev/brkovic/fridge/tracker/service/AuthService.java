package dev.brkovic.fridge.tracker.service;

import dev.brkovic.fridge.api.model.*;

public interface AuthService {
    AuthResponse register(RegisterRequest registerRequest);
    AuthResponse login(LoginRequest loginRequest);
    AuthResponse refreshToken(RefreshTokenRequest refreshTokenRequest);
    void logout();
    void removeExpiredRefreshTokens();
}
