package dev.brkovic.fridge.tracker.job;

import dev.brkovic.fridge.tracker.service.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RefreshTokenExpiryJobTest {
    @InjectMocks
    private RefreshTokenExpiryJob refreshTokenExpiryJob;

    @Mock
    private AuthService authService;

    @Test
    void executeJob(){
        // when
        refreshTokenExpiryJob.executeJob();

        // then
        verify(authService, times(1)).removeExpiredRefreshTokens();
    }
}
