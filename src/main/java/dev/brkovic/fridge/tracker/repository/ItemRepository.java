package dev.brkovic.fridge.tracker.repository;

import dev.brkovic.fridge.tracker.entity.ItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<ItemEntity, String> {
    Optional<ItemEntity> findByIdAndUserId(String id, String userId);
    List<ItemEntity> findByUserId(String userId);

    @Query(value = "SELECT i.name from item i WHERE i.id = :id", nativeQuery = true)
    String findItemNameById(String id);
}
