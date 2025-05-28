package dev.brkovic.fridge.tracker.api;

import dev.brkovic.fridge.api.ItemsApi;
import dev.brkovic.fridge.api.model.CreateItemRequest;
import dev.brkovic.fridge.api.model.ItemResponse;
import dev.brkovic.fridge.api.model.UpdateItemRequest;
import dev.brkovic.fridge.tracker.service.ItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ItemsApiImpl implements ItemsApi {

    private final ItemService itemService;

    @Override
    public ResponseEntity<ItemResponse> createItem(CreateItemRequest createItemRequest) {
        log.info("Received create item request: {}", createItemRequest);
        ItemResponse response = itemService.createItem(createItemRequest);

        log.info("Successful created item. Returning response: {}", response);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    public ResponseEntity<Void> deleteItem(String itemId) {
        log.info("Received delete item request: {}", itemId);

        itemService.deleteItem(itemId);

        log.info("Successful delete item with id: {}", itemId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<List<ItemResponse>> getAllItems() {
        log.info("Received get all items request");

        List<ItemResponse> response = itemService.getAllItems();

        log.info("Successfully fetched {} items", response.size());
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<ItemResponse> getItem(String itemId) {
        log.info("Received get item request for itemId: {}", itemId);

        ItemResponse response = itemService.getItem(itemId);

        log.info("Successfully fetched item with id: {}. Returning response: {}", itemId, response);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<ItemResponse> updateItem(String itemId, UpdateItemRequest updateItemRequest) {
        log.info("Received update item request: {} for itemId: {}", updateItemRequest, itemId);

        ItemResponse response = itemService.updateItem(itemId, updateItemRequest);

        log.info("Successfully updated item with id: {}. Returning response: {}", itemId, response);
        return ResponseEntity.ok(response);
    }
}
