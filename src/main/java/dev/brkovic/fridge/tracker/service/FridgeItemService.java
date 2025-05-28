package dev.brkovic.fridge.tracker.service;

import dev.brkovic.fridge.api.model.AddFridgeItemRequest;
import dev.brkovic.fridge.api.model.FridgeItemResponse;
import dev.brkovic.fridge.api.model.UpdateFridgeItemRequest;

import java.util.List;

public interface FridgeItemService {
    FridgeItemResponse addItemToFridge(String fridgeId, AddFridgeItemRequest addFridgeItemRequest);
    List<FridgeItemResponse> getFridgeItems(String fridgeId);
    void removeItemFromFridge(String fridgeId, String fridgeItemId);
    FridgeItemResponse updateFridgeItem(String fridgeId, String fridgeItemId, UpdateFridgeItemRequest updateFridgeItemRequest);
}
