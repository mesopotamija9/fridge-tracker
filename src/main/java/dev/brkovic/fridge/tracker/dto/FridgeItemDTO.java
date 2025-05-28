package dev.brkovic.fridge.tracker.dto;

import org.springframework.beans.factory.annotation.Value;

import java.util.Date;

public interface FridgeItemDTO {
    @Value("#{target.id}")
    String getId();

    @Value("#{target.fridge_id}")
    String getFridgeId();

    @Value("#{target.item_id}")
    String getItemId();

    @Value("#{target.stored_at}")
    Date getStoredAt();

    @Value("#{target.best_before_date}")
    Date getBestBeforeDate();

    @Value("#{target.item_name}")
    String getItemName();
}
