package dev.brkovic.fridge.tracker.repository;

import dev.brkovic.fridge.tracker.entity.FridgeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FridgeRepository extends JpaRepository<FridgeEntity, String> {
    Optional<FridgeEntity> findByIdAndUserId(String id, String userId);
    List<FridgeEntity> findByUserId(String userId);
}
