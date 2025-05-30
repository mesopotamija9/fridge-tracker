package dev.brkovic.fridge.tracker.service.impl;

import dev.brkovic.fridge.tracker.entity.UserEntity;
import dev.brkovic.fridge.tracker.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {
    @InjectMocks
    private CustomUserDetailsService userDetailsService;

    @Mock
    private UserRepository userRepository;

    private static final String USERNAME = "testUser";
    private static final String PASSWORD = "testPassword";

    private UserEntity testUser;

    @BeforeEach
    void setUp() {
        testUser = new UserEntity();
        testUser.setUsername(USERNAME);
        testUser.setPassword(PASSWORD);
    }

    @Test
    void loadUserByUsername_WhenUserExists_ThenSuccess() {
        // when
        when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(testUser));

        UserDetails userDetails = userDetailsService.loadUserByUsername(USERNAME);

        // then
        assertNotNull(userDetails);
        assertEquals(USERNAME, userDetails.getUsername());
        assertEquals(PASSWORD, userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().isEmpty());
    }

    @Test
    void loadUserByUsername_WhenUserNotFound_ThenThrowException() {
        // when
        when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.empty());

        // then
        assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername(USERNAME));
    }
} 