package dev.brkovic.fridge.tracker.repository;

import dev.brkovic.fridge.tracker.entity.RefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, String> {
    Optional<RefreshTokenEntity> findByToken(String token);
    void deleteByUsername(String username);

    @Modifying
    @Query(value = "DELETE FROM refresh_token WHERE expires_at <= CURRENT_TIMESTAMP", nativeQuery = true)
    int removeExpiredRefreshTokens();

    @Modifying
    @Query(value = "DELETE FROM refresh_token WHERE username = :username", nativeQuery = true)
    int removeRefreshTokensForUsername(String username);
}
