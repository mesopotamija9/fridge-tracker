package dev.brkovic.fridge.tracker.service.impl;

import dev.brkovic.fridge.api.model.*;
import dev.brkovic.fridge.tracker.configuration.JwtConfiguration;
import dev.brkovic.fridge.tracker.entity.RefreshTokenEntity;
import dev.brkovic.fridge.tracker.entity.UserEntity;
import dev.brkovic.fridge.tracker.exception.InternalException;
import dev.brkovic.fridge.tracker.repository.RefreshTokenRepository;
import dev.brkovic.fridge.tracker.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {
    @InjectMocks
    private AuthServiceImpl authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private JwtTokenProviderServiceImpl jwtTokenProviderService;

    @Mock
    private JwtConfiguration jwtConfiguration;

    @Mock
    private PasswordEncoder passwordEncoder;

    private static final String USERNAME = "testUser";
    private static final String PASSWORD = "testPassword";
    private static final String ACCESS_TOKEN = "accessTokenTest";
    private static final String REFRESH_TOKEN = "refreshTokenTest";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(authService, "userRepository", userRepository);
    }

    @Test
    void register_WhenValidRequest_ThenSuccess() {
        // given
        RegisterRequest request = new RegisterRequest()
                .username(USERNAME)
                .password(PASSWORD)
                .passwordConfirmation(PASSWORD);
        
        UserEntity savedUser = new UserEntity();
        savedUser.setId("1");
        savedUser.setUsername(USERNAME);
        savedUser.setPassword("encodedPassword");

        // when
        when(userRepository.existsByUsername(USERNAME)).thenReturn(false);
        when(passwordEncoder.encode(PASSWORD)).thenReturn("encodedPassword");
        when(userRepository.save(any(UserEntity.class))).thenReturn(savedUser);
        when(jwtTokenProviderService.generateAccessToken(USERNAME)).thenReturn(ACCESS_TOKEN);
        when(jwtTokenProviderService.generateRefreshToken()).thenReturn(REFRESH_TOKEN);

        AuthResponse response = authService.register(request);

        // then
        assertNotNull(response);
        assertNotNull(response.getTokens());
        assertEquals(ACCESS_TOKEN, response.getTokens().getAccessToken());
        assertEquals(REFRESH_TOKEN, response.getTokens().getRefreshToken());
        verify(refreshTokenRepository).save(any(RefreshTokenEntity.class));
    }

    @Test
    void register_WhenUsernameExists_ThenThrowException() {
        // given
        RegisterRequest request = new RegisterRequest()
                .username(USERNAME)
                .password(PASSWORD)
                .passwordConfirmation(PASSWORD);

        // when
        when(userRepository.existsByUsername(USERNAME)).thenReturn(true);

        // then
        assertThrows(InternalException.class, () -> authService.register(request));
        verify(userRepository, never()).save(any());
    }

    @Test
    void login_WhenValidCredentials_ThenSuccess() {
        // given
        LoginRequest request = new LoginRequest()
                .username(USERNAME)
                .password(PASSWORD);

        UserEntity user = new UserEntity();
        user.setUsername(USERNAME);
        user.setPassword("encodedPassword");

        // when
        when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(PASSWORD, "encodedPassword")).thenReturn(true);
        when(jwtTokenProviderService.generateAccessToken(USERNAME)).thenReturn(ACCESS_TOKEN);
        when(jwtTokenProviderService.generateRefreshToken()).thenReturn(REFRESH_TOKEN);

        AuthResponse response = authService.login(request);

        // then
        assertNotNull(response);
        assertNotNull(response.getTokens());
        assertEquals(ACCESS_TOKEN, response.getTokens().getAccessToken());
        assertEquals(REFRESH_TOKEN, response.getTokens().getRefreshToken());
        verify(refreshTokenRepository).deleteByUsername(USERNAME);
        verify(refreshTokenRepository).save(any(RefreshTokenEntity.class));
    }

    @Test
    void refreshToken_WhenValidToken_ThenSuccess() {
        // given
        RefreshTokenRequest request = new RefreshTokenRequest()
                .refreshToken(REFRESH_TOKEN);

        RefreshTokenEntity refreshTokenEntity = new RefreshTokenEntity();
        refreshTokenEntity.setToken(REFRESH_TOKEN);
        refreshTokenEntity.setUsername(USERNAME);
        refreshTokenEntity.setExpiresAt(LocalDateTime.now().plusDays(1));

        // when
        when(refreshTokenRepository.findByToken(REFRESH_TOKEN)).thenReturn(Optional.of(refreshTokenEntity));
        when(jwtTokenProviderService.generateAccessToken(USERNAME)).thenReturn(ACCESS_TOKEN);
        when(jwtTokenProviderService.generateRefreshToken()).thenReturn("newRefreshToken");
        when(refreshTokenRepository.save(any(RefreshTokenEntity.class))).thenReturn(refreshTokenEntity);
        when(jwtConfiguration.getRefreshExpirationMs()).thenReturn(86400000L);

        AuthResponse response = authService.refreshToken(request);

        // then
        assertNotNull(response);
        assertNotNull(response.getTokens());
        assertEquals(ACCESS_TOKEN, response.getTokens().getAccessToken());
        assertNotEquals(REFRESH_TOKEN, response.getTokens().getRefreshToken());
        verify(refreshTokenRepository).save(any(RefreshTokenEntity.class));
    }

    @Test
    void refreshToken_WhenInvalidToken_ThenThrowException() {
        // given
        RefreshTokenRequest request = new RefreshTokenRequest()
                .refreshToken(REFRESH_TOKEN);

        // when
        when(refreshTokenRepository.findByToken(REFRESH_TOKEN)).thenReturn(Optional.empty());

        // then
        assertThrows(InternalException.class, () -> authService.refreshToken(request));
        verify(refreshTokenRepository, never()).save(any());
    }

    @Test
    void removeExpiredRefreshTokens_ThenSuccess() {
        // given
        when(refreshTokenRepository.removeExpiredRefreshTokens()).thenReturn(2);

        // when
        authService.removeExpiredRefreshTokens();

        // then
        verify(refreshTokenRepository).removeExpiredRefreshTokens();
    }
} 