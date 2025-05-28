package dev.brkovic.fridge.tracker.service;

import dev.brkovic.fridge.api.model.CreateFridgeRequest;
import dev.brkovic.fridge.api.model.FridgeResponse;
import dev.brkovic.fridge.api.model.UpdateFridgeRequest;

import java.util.List;

public interface FridgeService {
    FridgeResponse createFridge(CreateFridgeRequest createFridgeRequest);
    void deleteFridge(String fridgeId);
    List<FridgeResponse> getAllFridges();
    FridgeResponse getFridge(String fridgeId);
    FridgeResponse updateFridge(String fridgeId, UpdateFridgeRequest updateFridgeRequest);
}
