package dev.brkovic.fridge.tracker.api;

import dev.brkovic.fridge.api.FridgesApi;
import dev.brkovic.fridge.api.model.CreateFridgeRequest;
import dev.brkovic.fridge.api.model.FridgeResponse;
import dev.brkovic.fridge.api.model.UpdateFridgeRequest;
import dev.brkovic.fridge.tracker.service.FridgeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class FridgesApiImpl implements FridgesApi {

    private final FridgeService fridgeService;

    @Override
    public ResponseEntity<FridgeResponse> createFridge(CreateFridgeRequest createFridgeRequest) {
        log.info("Received create fridge request: {}", createFridgeRequest);

        FridgeResponse response = fridgeService.createFridge(createFridgeRequest);

        log.info("Successfully created fridge. Returning response: {}", response);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    public ResponseEntity<Void> deleteFridge(String fridgeId) {
        log.info("Received delete fridge request for fridgeId: {}", fridgeId);

        fridgeService.deleteFridge(fridgeId);

        log.info("Successfully deleted fridge with fridgeId: {}", fridgeId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<List<FridgeResponse>> getAllFridges() {
        log.info("Received get all fridges request");

        List<FridgeResponse> response = fridgeService.getAllFridges();

        log.info("Successfully fetched {} fridges", response.size());
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<FridgeResponse> getFridge(String fridgeId) {
        log.info("Received get fridge request for fridgeId: {}", fridgeId);

        FridgeResponse response = fridgeService.getFridge(fridgeId);

        log.info("Successfully fetched fridge with fridgeId: {}. Returning response: {}", fridgeId, response);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<FridgeResponse> updateFridge(String fridgeId, UpdateFridgeRequest updateFridgeRequest) {
        log.info("Received update fridge request: {} for fridgeId: {}", updateFridgeRequest, fridgeId);

        FridgeResponse response = fridgeService.updateFridge(fridgeId, updateFridgeRequest);

        log.info("Successfully updated fridge with fridgeId: {}. Returning response: {}", fridgeId, response);
        return ResponseEntity.ok(response);
    }
}
