package dev.brkovic.fridge.tracker.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtConfiguration {
    private String secret;
    private long accessExpirationMs;
    private long refreshExpirationMs;
}
