package dev.brkovic.fridge.tracker.service.impl;

import dev.brkovic.fridge.api.model.CreateFridgeRequest;
import dev.brkovic.fridge.api.model.FridgeResponse;
import dev.brkovic.fridge.api.model.UpdateFridgeRequest;
import dev.brkovic.fridge.tracker.entity.FridgeEntity;
import dev.brkovic.fridge.tracker.entity.UserEntity;
import dev.brkovic.fridge.tracker.exception.InternalException;
import dev.brkovic.fridge.tracker.mapper.FridgeMapper;
import dev.brkovic.fridge.tracker.repository.FridgeItemRepository;
import dev.brkovic.fridge.tracker.repository.FridgeRepository;
import dev.brkovic.fridge.tracker.service.FridgeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FridgeServiceImpl extends BaseService implements FridgeService {

    private final FridgeRepository fridgeRepository;
    private final FridgeItemRepository fridgeItemRepository;
    private final FridgeMapper fridgeMapper;

    @Override
    @Transactional
    public FridgeResponse createFridge(CreateFridgeRequest createFridgeRequest) {
        log.info("Creating new fridge for request: {}", createFridgeRequest);

        UserEntity loggedInUser = getLoggedInUser();

        FridgeEntity fridge = new FridgeEntity();
        fridge.setName(createFridgeRequest.getName());
        fridge.setUserId(loggedInUser.getId());

        fridge = fridgeRepository.saveAndFlush(fridge);
        log.info("Successfully created fridge with ID: {}", fridge.getId());

        return fridgeMapper.fridgeEntityToFridgeResponse(fridge);
    }

    @Override
    @Transactional
    public void deleteFridge(String fridgeId) {
        log.info("Deleting fridge with ID: {}", fridgeId);

        UserEntity loggedInUser = getLoggedInUser();

        FridgeEntity fridge = fridgeRepository.findByIdAndUserId(fridgeId, loggedInUser.getId()).orElse(null);
        if (fridge == null){
            log.info("Fridge not found with ID: {} for user ID: {}", fridgeId, loggedInUser.getId());
            throw new InternalException(String.format("Fridge not found with ID: %s", fridgeId));
        }

        int fridgeItemCount = fridgeItemRepository.countItemsInFridge(fridgeId);
        if (fridgeItemCount > 0) {
            log.info("Can not delete fridge with ID: {} because it contains {} items", fridgeId, fridgeItemCount);
            throw new InternalException("Can not delete fridge that contains items");
        }

        fridgeRepository.delete(fridge);
        log.info("Successfully deleted fridge with ID: {}", fridgeId);
    }

    @Override
    public List<FridgeResponse> getAllFridges() {
        log.info("Fetching all fridges for current user");

        UserEntity loggedInUser = getLoggedInUser();
        List<FridgeEntity> fridges = fridgeRepository.findByUserId(loggedInUser.getId());

        log.info("Found {} fridges for user with username: {}", fridges.size(), loggedInUser.getUsername());

        return fridges.stream()
                .map(fridgeMapper::fridgeEntityToFridgeResponse)
                .toList();
    }

    @Override
    public FridgeResponse getFridge(String fridgeId) {
        log.info("Fetching fridge with ID: {}", fridgeId);

        UserEntity loggedInUser = getLoggedInUser();
        FridgeEntity fridge = fridgeRepository.findByIdAndUserId(fridgeId, loggedInUser.getId()).orElse(null);
        if (fridge == null) {
            log.info("Unable to find fridge with id: {} for user with id: {}", fridgeId, loggedInUser.getId());
            throw new InternalException(String.format("Fridge not found with ID: %s", fridgeId));
        }

        log.info("Successfully retrieved fridge: {}", fridge);
        return fridgeMapper.fridgeEntityToFridgeResponse(fridge);
    }

    @Override
    @Transactional
    public FridgeResponse updateFridge(String fridgeId, UpdateFridgeRequest updateFridgeRequest) {
        log.info("Updating fridge with ID: {} with request: {}", fridgeId, updateFridgeRequest);

        UserEntity loggedInUser = getLoggedInUser();
        FridgeEntity fridge = fridgeRepository.findByIdAndUserId(fridgeId, loggedInUser.getId()).orElse(null);
        if (fridge == null) {
            log.info("Fridge not found with ID: {} for user ID: {}", fridgeId, loggedInUser.getId());
            throw new InternalException(String.format("Fridge not found with ID: %s", fridgeId));
        }

        fridge.setName(updateFridgeRequest.getName());
        FridgeEntity updatedFridge = fridgeRepository.save(fridge);

        log.info("Successfully updated fridge with ID: {}", fridgeId);
        return fridgeMapper.fridgeEntityToFridgeResponse(updatedFridge);
    }
}
