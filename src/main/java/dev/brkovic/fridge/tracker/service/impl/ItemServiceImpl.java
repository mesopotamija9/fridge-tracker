package dev.brkovic.fridge.tracker.service.impl;

import dev.brkovic.fridge.api.model.CreateItemRequest;
import dev.brkovic.fridge.api.model.ItemResponse;
import dev.brkovic.fridge.api.model.UpdateItemRequest;
import dev.brkovic.fridge.tracker.entity.ItemEntity;
import dev.brkovic.fridge.tracker.entity.UserEntity;
import dev.brkovic.fridge.tracker.exception.InternalException;
import dev.brkovic.fridge.tracker.mapper.ItemMapper;
import dev.brkovic.fridge.tracker.repository.ItemRepository;
import dev.brkovic.fridge.tracker.service.ItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl extends BaseService implements ItemService {

    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;

    @Override
    public ItemResponse createItem(CreateItemRequest createItemRequest) {
        log.info("Creating new item for createItemRequest: {}", createItemRequest);

        UserEntity loggedInUsers = getLoggedInUser();

        ItemEntity item = new ItemEntity();
        item.setName(createItemRequest.getName());
        item.setUserId(loggedInUsers.getId());

        item = itemRepository.save(item);
        log.info("Successfully created new item: {}", item);

        return itemMapper.itemEntityToItemResponse(item);
    }

    @Override
    public ItemResponse getItem(String itemId) {
        log.info("Fetching item with ID: {}", itemId);

        UserEntity loggedInUser = getLoggedInUser();

        ItemEntity item = itemRepository.findByIdAndUserId(itemId, loggedInUser.getId()).orElse(null);
        if (item == null) {
            log.info("Unable to find item with id: {} for user with id: {}", itemId, loggedInUser.getId());
            throw new InternalException(String.format("Item not found with ID: %s", itemId));
        }

        log.info("Successfully fetched item: {}", item);

        return itemMapper.itemEntityToItemResponse(item);
    }

    @Override
    public List<ItemResponse> getAllItems() {
        UserEntity loggedInUser = getLoggedInUser();

        log.info("Fetching all items for current user with username: {}", loggedInUser.getUsername());

        List<ItemEntity> items = itemRepository.findByUserId(loggedInUser.getId());
        if (items.isEmpty()) {
            log.info("No items found for user with ID: {}", loggedInUser.getId());
        }

        log.info("Successfully fetched {} items", items.size());

        return items.stream()
                .map(itemMapper::itemEntityToItemResponse)
                .toList();
    }

    @Override
    public ItemResponse updateItem(String itemId, UpdateItemRequest updateItemRequest) {
        log.info("Updating item with ID: {} using request: {}", itemId, updateItemRequest);

        UserEntity loggedInUser = getLoggedInUser();

        ItemEntity item = itemRepository.findByIdAndUserId(itemId, loggedInUser.getId()).orElse(null);
        if (item == null) {
            log.info("Item not found with ID: {} for user ID: {}", itemId, loggedInUser.getId());
            throw new InternalException(String.format("Item not found with ID: %s", itemId));
        }

        item.setName(updateItemRequest.getName());
        item = itemRepository.save(item);

        log.info("Successfully updated item: {}", item);

        return itemMapper.itemEntityToItemResponse(item);
    }

    @Override
    public void deleteItem(String itemId) {
        log.info("Deleting item with ID: {}", itemId);

        UserEntity loggedInUser = getLoggedInUser();

        ItemEntity item = itemRepository.findByIdAndUserId(itemId, loggedInUser.getId()).orElse(null);
        if (item == null) {
            log.info("Item not found with ID: {} for user ID: {}", itemId, loggedInUser.getId());
            throw new InternalException(String.format("Item not found with ID: %s", itemId));
        }

        itemRepository.delete(item);
        log.info("Successfully deleted item with ID: {}", itemId);
    }
}
