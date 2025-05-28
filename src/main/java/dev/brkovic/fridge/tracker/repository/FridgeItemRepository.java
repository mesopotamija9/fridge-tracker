package dev.brkovic.fridge.tracker.repository;

import dev.brkovic.fridge.tracker.dto.FridgeItemDTO;
import dev.brkovic.fridge.tracker.entity.FridgeItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FridgeItemRepository extends JpaRepository<FridgeItemEntity, String> {

    @Query(value = "SELECT fi.id, fi.fridge_id, fi.item_id, fi.best_before_date, fi.stored_at, i.name AS item_name " +
            "FROM fridge_item fi JOIN item i ON i.id = fi.item_id " +
            "WHERE fi.fridge_id = :fridgeId " +
            "ORDER BY fi.best_before_date", nativeQuery = true)
    List<FridgeItemDTO> findByFridgeId(String fridgeId);

    @Query(value = "SELECT fi.id, fi.fridge_id, fi.item_id, fi.best_before_date, fi.stored_at, i.name AS item_name " +
            "FROM fridge_item fi JOIN item i ON i.id = fi.item_id " +
            "WHERE fi.id = :fridgeItemId", nativeQuery = true)
    FridgeItemDTO findByFridgeItemId(String fridgeItemId);

    @Modifying
    @Query(value = "DELETE FROM fridge_item WHERE id = :id", nativeQuery = true)
    void deleteFridgeItem(String id);

    @Query(value = "SELECT COUNT(*) FROM fridge_item fi WHERE fi.fridge_id = :fridgeId", nativeQuery = true)
    int countItemsInFridge(String fridgeId);

    @Query(value = "SELECT COUNT(*) FROM fridge_item fi WHERE fi.item_id = :itemId", nativeQuery = true)
    int countFridgesForItemId(String itemId);
}
