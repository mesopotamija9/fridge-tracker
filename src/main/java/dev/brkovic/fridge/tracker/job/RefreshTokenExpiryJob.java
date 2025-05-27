package dev.brkovic.fridge.tracker.job;

import dev.brkovic.fridge.tracker.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RefreshTokenExpiryJob {
    private final AuthService authService;

    @Scheduled(cron = "${jwt.refresh-token-expiry.cron}")
    public void executeJob(){
        log.info("Started RefreshTokenExpiryJob");

        authService.removeExpiredRefreshTokens();

        log.info("Finished RefreshTokenExpiryJob");
    }
}
