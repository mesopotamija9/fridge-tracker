package dev.brkovic.fridge.tracker.service;

import dev.brkovic.fridge.api.model.CreateItemRequest;
import dev.brkovic.fridge.api.model.ItemResponse;
import dev.brkovic.fridge.api.model.UpdateItemRequest;

import java.util.List;

public interface ItemService {
    ItemResponse createItem(CreateItemRequest createItemRequest);
    ItemResponse getItem(String itemId);
    List<ItemResponse> getAllItems();
    ItemResponse updateItem(String itemId, UpdateItemRequest updateItemRequest);
    void deleteItem(String itemId);

}
