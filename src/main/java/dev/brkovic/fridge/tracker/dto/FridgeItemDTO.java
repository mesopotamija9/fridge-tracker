package dev.brkovic.fridge.tracker.dto;

import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDate;

public interface FridgeItemDTO {
    @Value("#{target.id}")
    String getId();

    @Value("#{target.fridge_id}")
    String getFridgeId();

    @Value("#{target.item_id}")
    String getItemId();

    @Value("#{target.stored_at}")
    LocalDate getStoredAt();

    @Value("#{target.best_before_date}")
    LocalDate getBestBeforeDate();

    @Value("#{target.item_name}")
    String getItemName();
}
