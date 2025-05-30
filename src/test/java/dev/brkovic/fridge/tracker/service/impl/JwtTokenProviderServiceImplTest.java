package dev.brkovic.fridge.tracker.service.impl;

import dev.brkovic.fridge.tracker.configuration.JwtConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtTokenProviderServiceImplTest {
    @InjectMocks
    private JwtTokenProviderServiceImpl jwtTokenProviderService;

    @Mock
    private JwtConfiguration jwtConfiguration;

    private static final String USERNAME = "testUser";
    private static final String SECRET = "jjkhdJKFUI7843hbsKJ897329idfjdsh8493yKJhjkhfds9847239";
    private static final long ACCESS_EXPIRATION_MS = 3600000;

    @BeforeEach
    void setUp() {
        lenient().when(jwtConfiguration.getSecret()).thenReturn(SECRET);
        lenient().when(jwtConfiguration.getAccessExpirationMs()).thenReturn(ACCESS_EXPIRATION_MS);
    }

    @Test
    void generateAccessToken_WhenValidUsername_ThenSuccess() {
        // when
        when(jwtConfiguration.getSecret()).thenReturn(SECRET);
        when(jwtConfiguration.getAccessExpirationMs()).thenReturn(ACCESS_EXPIRATION_MS);

        String token = jwtTokenProviderService.generateAccessToken(USERNAME);

        // then
        assertNotNull(token);
        assertTrue(jwtTokenProviderService.validateToken(token));
        assertEquals(USERNAME, jwtTokenProviderService.getUsername(token));
    }

    @Test
    void generateRefreshToken_ThenSuccess() {
        // when
        String refreshToken = jwtTokenProviderService.generateRefreshToken();

        // then
        assertNotNull(refreshToken);
        assertFalse(refreshToken.isEmpty());
    }

    @Test
    void validateToken_WhenValidToken_ThenSuccess() {
        // when
        when(jwtConfiguration.getSecret()).thenReturn(SECRET);
        when(jwtConfiguration.getAccessExpirationMs()).thenReturn(ACCESS_EXPIRATION_MS);

        String token = jwtTokenProviderService.generateAccessToken(USERNAME);
        boolean isValid = jwtTokenProviderService.validateToken(token);

        // then
        assertTrue(isValid);
    }

    @Test
    void validateToken_WhenInvalidToken_ThenReturnFalse() {
        // given
        String invalidToken = "invalidToken";

        // when
        when(jwtConfiguration.getSecret()).thenReturn(SECRET);

        boolean isValid = jwtTokenProviderService.validateToken(invalidToken);

        // then
        assertFalse(isValid);
    }

    @Test
    void getUsername_WhenValidToken_ThenSuccess() {
        // when
        when(jwtConfiguration.getSecret()).thenReturn(SECRET);
        when(jwtConfiguration.getAccessExpirationMs()).thenReturn(ACCESS_EXPIRATION_MS);
        String token = jwtTokenProviderService.generateAccessToken(USERNAME);

        String username = jwtTokenProviderService.getUsername(token);

        // then
        assertEquals(USERNAME, username);
    }
} 