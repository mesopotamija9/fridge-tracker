package dev.brkovic.fridge.tracker.service.impl;

import dev.brkovic.fridge.api.model.AddFridgeItemRequest;
import dev.brkovic.fridge.api.model.FridgeItemResponse;
import dev.brkovic.fridge.api.model.UpdateFridgeItemRequest;
import dev.brkovic.fridge.tracker.dto.FridgeItemDTO;
import dev.brkovic.fridge.tracker.entity.FridgeEntity;
import dev.brkovic.fridge.tracker.entity.FridgeItemEntity;
import dev.brkovic.fridge.tracker.entity.ItemEntity;
import dev.brkovic.fridge.tracker.entity.UserEntity;
import dev.brkovic.fridge.tracker.enums.ExceptionSeverityLevel;
import dev.brkovic.fridge.tracker.exception.InternalException;
import dev.brkovic.fridge.tracker.mapper.FridgeItemMapper;
import dev.brkovic.fridge.tracker.repository.FridgeItemRepository;
import dev.brkovic.fridge.tracker.repository.FridgeRepository;
import dev.brkovic.fridge.tracker.repository.ItemRepository;
import dev.brkovic.fridge.tracker.service.FridgeItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FridgeItemServiceImpl extends BaseService implements FridgeItemService {

    private final FridgeRepository fridgeRepository;
    private final ItemRepository itemRepository;
    private final FridgeItemRepository fridgeItemRepository;
    private final FridgeItemMapper fridgeItemMapper;

    @Override
    @Transactional
    public FridgeItemResponse addItemToFridge(String fridgeId, AddFridgeItemRequest addFridgeItemRequest) {
        log.info("Adding item to fridge ID: {} with request: {}", fridgeId, addFridgeItemRequest);

        UserEntity user = getLoggedInUser();

        FridgeEntity fridge = fridgeRepository.findByIdAndUserId(fridgeId, user.getId()).orElse(null);
        if (fridge == null) {
            log.info("Fridge not found with ID: {} for user with username: {}", fridgeId, user.getUsername());
            throw new InternalException(String.format("Fridge not found with ID: %s", fridgeId));
        }

        ItemEntity item = itemRepository.findByIdAndUserId(addFridgeItemRequest.getItemId(), user.getId()).orElse(null);
        if (item == null) {
            log.info("Item not found with ID: {} for user ID: {}", addFridgeItemRequest.getItemId(), user.getId());
            throw new InternalException(String.format("Item not found with ID: %s", addFridgeItemRequest.getItemId()));
        }

        FridgeItemEntity fridgeItem = new FridgeItemEntity();
        fridgeItem.setFridgeId(fridge.getId());
        fridgeItem.setItemId(item.getId());
        fridgeItem.setStoredAt(addFridgeItemRequest.getStoredAt());
        fridgeItem.setBestBeforeDate(addFridgeItemRequest.getBestBeforeDate());

        fridgeItem = fridgeItemRepository.saveAndFlush(fridgeItem);
        log.info("Added item {} to fridge {}", item.getId(), fridgeId);

        return fridgeItemMapper.fridgeItemEntityToFridgeItemResponse(fridgeItem, item.getName());
    }

    @Override
    public List<FridgeItemResponse> getFridgeItems(String fridgeId) {
        log.info("Fetching items for fridgeId: {}", fridgeId);

        UserEntity user = getLoggedInUser();

        FridgeEntity fridge = fridgeRepository.findByIdAndUserId(fridgeId, user.getId()).orElse(null);
        if (fridge == null) {
            log.info("Fridge not found with ID: {} for user ID: {}", fridgeId, user.getId());
            throw new InternalException(String.format("Fridge not found with ID: %s", fridgeId));
        }

        List<FridgeItemDTO> items = fridgeItemRepository.findByFridgeId(fridgeId);
        log.info("Found {} items in fridge {}", items.size(), fridgeId);

        return items.stream()
                .map(fridgeItemMapper::fridgeItemDTOToFridgeItemResponse)
                .toList();
    }

    @Override
    @Transactional
    public void removeItemFromFridge(String fridgeId, String fridgeItemId) {
        log.info("Removing item with id {} from fridge with id {}", fridgeItemId, fridgeId);

        UserEntity user = getLoggedInUser();

        FridgeItemDTO fridgeItem = fridgeItemRepository.findByFridgeItemId(fridgeItemId);
        if (fridgeItem == null) {
            log.info("Fridge item not found with ID: {}", fridgeItemId);
            throw new InternalException(String.format("Fridge item not found with ID: %s", fridgeItemId));
        }

        FridgeEntity fridge = fridgeRepository.findByIdAndUserId(fridgeId, user.getId()).orElse(null);
        if (fridge == null){
            log.info("Fridge not found with ID: {} for user ID: {}", fridgeId, user.getId());
            throw new InternalException(String.format("Fridge not found with ID: %s", fridgeId));
        }

        if (!fridgeItem.getFridgeId().equals(fridge.getId())) {
            log.info("Item with ID: {} not found in fridge with ID: {}", fridgeItemId, fridge.getId());
            throw new InternalException(String.format("Fridge item not found with ID: %s", fridgeItemId));
        }

        fridgeItemRepository.deleteFridgeItem(fridgeItem.getId());
        log.info("Removed item {} from fridge {}", fridgeItemId, fridgeId);
    }

    @Override
    @Transactional
    public FridgeItemResponse updateFridgeItem(String fridgeId, String fridgeItemId, UpdateFridgeItemRequest updateFridgeItemRequest) {
        log.info("Updating fridge item with id: {} in fridge with id: {} with request: {}", fridgeItemId, fridgeId, updateFridgeItemRequest);

        UserEntity user = getLoggedInUser();

        FridgeItemEntity fridgeItem = fridgeItemRepository.findById(fridgeItemId).orElse(null);
        if (fridgeItem == null) {
            log.info("Fridge item not found with ID: {}", fridgeItemId);
            throw new InternalException(String.format("Fridge item not found with ID: %s", fridgeItemId));
        }

        FridgeEntity fridge = fridgeRepository.findByIdAndUserId(fridgeId, user.getId()).orElse(null);
        if (fridge == null){
            log.info("Fridge not found with ID: {} for user ID: {}", fridgeId, user.getId());
            throw new InternalException(String.format("Fridge not found with ID: %s", fridgeId));
        }

        if (!fridgeItem.getFridgeId().equals(fridge.getId())) {
            log.info("User with username {} does not own fridge item {}", user.getUsername(), fridgeItemId);
            throw new InternalException(String.format("Fridge item not found in fridge with ID: %s", fridgeId));
        }

        fridgeItem.setStoredAt(updateFridgeItemRequest.getStoredAt());
        fridgeItem.setBestBeforeDate(updateFridgeItemRequest.getBestBeforeDate());
        fridgeItem = fridgeItemRepository.save(fridgeItem);
        log.info("Updated fridge item with id: {}", fridgeItemId);

        String itemName = itemRepository.findItemNameById(fridgeItem.getItemId());
        if (itemName == null) {
            log.warn("Could not find item name for item with id: {}", fridgeItem.getItemId());
            throw new InternalException(String.format("Could not find item name for item with id: %s", fridgeItem.getItemId()),
                    ExceptionSeverityLevel.ERROR);
        }

        return fridgeItemMapper.fridgeItemEntityToFridgeItemResponse(fridgeItem, itemName);
    }
}
