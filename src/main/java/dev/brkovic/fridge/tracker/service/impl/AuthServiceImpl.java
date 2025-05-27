package dev.brkovic.fridge.tracker.service.impl;

import dev.brkovic.fridge.api.model.*;
import dev.brkovic.fridge.tracker.configuration.JwtConfiguration;
import dev.brkovic.fridge.tracker.entity.RefreshTokenEntity;
import dev.brkovic.fridge.tracker.entity.UserEntity;
import dev.brkovic.fridge.tracker.exception.ValidationException;
import dev.brkovic.fridge.tracker.repository.RefreshTokenRepository;
import dev.brkovic.fridge.tracker.repository.UserRepository;
import dev.brkovic.fridge.tracker.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProviderServiceImpl jwtTokenProviderService;
    private final JwtConfiguration jwtConfiguration;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest registerRequest) {
        log.info("Processing registerRequest: {}", registerRequest);

        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            log.info("Yser with username: {} already exists", registerRequest.getUsername());

            throw new ValidationException("Username already exists", HttpStatus.CONFLICT);
        }

        UserEntity user = new UserEntity();
        user.setUsername(registerRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        userRepository.save(user);
        log.info("Created new user with username: {}", user.getUsername());

        String accessToken = jwtTokenProviderService.generateAccessToken(user.getUsername());
        String refreshToken = jwtTokenProviderService.generateRefreshToken();

        persistRefreshToken(refreshToken, user.getUsername());

        Tokens tokens = createTokens(accessToken, refreshToken);

        AuthResponse authResponse = new AuthResponse();
        authResponse.setTokens(tokens);

        log.info("Successfully registered new user with username: {}", user.getUsername());
        return authResponse;
    }

    @Override
    @Transactional
    public AuthResponse login(LoginRequest loginRequest) {
        log.info("Processing loginRequest: {}", loginRequest);

        UserEntity user = userRepository.findByUsername(loginRequest.getUsername()).orElse(null);
        if (user == null || !passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            log.info("Invalid credentials for username: {}", loginRequest.getUsername());
            throw new ValidationException("Invalid credentials");
        }

        String accessToken = jwtTokenProviderService.generateAccessToken(user.getUsername());
        String refreshToken = jwtTokenProviderService.generateRefreshToken();

        refreshTokenRepository.deleteByUsername(user.getUsername());
        log.info("Deleted old refresh token for username: {}", user.getUsername());

        persistRefreshToken(refreshToken, user.getUsername());

        Tokens tokens = createTokens(accessToken, refreshToken);

        AuthResponse authResponse = new AuthResponse();
        authResponse.setTokens(tokens);

        log.info("User with username: {} successfully logged in", user.getUsername());
        return authResponse;
    }

    @Override
    @Transactional
    public TokenResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        log.info("Processing refreshTokenRequest: {}", refreshTokenRequest);

        RefreshTokenEntity refreshTokenEntity = refreshTokenRepository.findByToken(refreshTokenRequest.getRefreshToken())
                .orElse(null);

        if (refreshTokenEntity == null){
            log.info("Unable to find refresh token: {}", refreshTokenRequest.getRefreshToken());
            throw new ValidationException("Invalid refresh token");
        }

        String newAccess = jwtTokenProviderService.generateAccessToken(refreshTokenEntity.getUsername());
        String newRefreshToken = jwtTokenProviderService.generateRefreshToken();

        refreshTokenEntity.setToken(newRefreshToken);
        refreshTokenEntity.setExpiresAt(LocalDateTime.now().plusSeconds(jwtConfiguration.getRefreshExpirationMs() / 1000));
        refreshTokenEntity = refreshTokenRepository.save(refreshTokenEntity);
        log.info("Updated refresh token: {}", refreshTokenEntity);

        TokenResponse tokenResponse = new TokenResponse();
        tokenResponse.setAccessToken(newAccess);
        tokenResponse.setRefreshToken(newRefreshToken);

        log.info("Successfully refreshed token: {}", refreshTokenRequest.getRefreshToken());
        return tokenResponse;
    }

    @Override
    @Transactional
    public void removeExpiredRefreshTokens(){
        log.info("Removing expired refresh tokens");

        int removedCount = refreshTokenRepository.removeExpiredRefreshTokens();

        log.info("Removed {} expired refresh tokens", removedCount);
    }

    private void persistRefreshToken(String refreshToken, String username){
        RefreshTokenEntity refreshTokenEntity = new RefreshTokenEntity();
        refreshTokenEntity.setToken(refreshToken);
        refreshTokenEntity.setUsername(username);
        refreshTokenEntity.setExpiresAt(LocalDateTime.now().plusSeconds(jwtConfiguration.getRefreshExpirationMs() / 1000));
        refreshTokenEntity = refreshTokenRepository.save(refreshTokenEntity);
        log.info("Created refresh token: {}", refreshTokenEntity);
    }

    private Tokens createTokens(String accessToken, String refreshToken) {
        Tokens tokens = new Tokens();

        tokens.setAccessToken(accessToken);
        tokens.setRefreshToken(refreshToken);

        return tokens;
    }
}
