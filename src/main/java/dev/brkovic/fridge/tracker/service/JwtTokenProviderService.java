package dev.brkovic.fridge.tracker.service;

public interface JwtTokenProviderService {
    String generateAccessToken(String username);
    String generateRefreshToken();
    boolean validateToken(String token);
    String getUsername(String token);
}
