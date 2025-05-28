package dev.brkovic.fridge.tracker.api;

import dev.brkovic.fridge.api.FridgeItemsApi;
import dev.brkovic.fridge.api.model.AddFridgeItemRequest;
import dev.brkovic.fridge.api.model.FridgeItemResponse;
import dev.brkovic.fridge.api.model.UpdateFridgeItemRequest;
import dev.brkovic.fridge.tracker.service.FridgeItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class FridgeItemsApiImpl implements FridgeItemsApi {

    private final FridgeItemService fridgeItemService;

    @Override
    public ResponseEntity<FridgeItemResponse> addItemToFridge(String fridgeId, AddFridgeItemRequest addFridgeItemRequest) {
        log.info("Received add item to fridge request: {} for fridgeId: {}", addFridgeItemRequest, fridgeId);

        FridgeItemResponse response = fridgeItemService.addItemToFridge(fridgeId, addFridgeItemRequest);

        log.info("Successful added item to fridge with id: {}. Returning response: {}", fridgeId, response);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    public ResponseEntity<List<FridgeItemResponse>> getFridgeItems(String fridgeId) {
        log.info("Received get fridge items request for fridgeId: {}", fridgeId);

        List<FridgeItemResponse> response = fridgeItemService.getFridgeItems(fridgeId);

        log.info("Successfully fetched {} items for fridgeId: {}", response.size(), fridgeId);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Void> removeItemFromFridge(String fridgeId, String fridgeItemId) {
        log.info("Received remove item from fridge request for fridgeId: {} and fridgeItemId: {}", fridgeId, fridgeItemId);

        fridgeItemService.removeItemFromFridge(fridgeId, fridgeItemId);

        log.info("Successfully removed fridgeItemId: {} from fridge with fridgeId: {}", fridgeItemId, fridgeId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<FridgeItemResponse> updateFridgeItem(String fridgeId, String fridgeItemId, UpdateFridgeItemRequest updateFridgeItemRequest) {
        log.info("Received update fridge item request: {} for fridgeId: {} and fridgeItemId: {}", updateFridgeItemRequest, fridgeId, fridgeItemId);

        FridgeItemResponse response = fridgeItemService.updateFridgeItem(fridgeId, fridgeItemId, updateFridgeItemRequest);

        log.info("Successfully updated fridgeItemId: {} for fridgeId: {}. Returning response: {}", fridgeItemId, fridgeId, response);
        return ResponseEntity.ok(response);
    }
}
