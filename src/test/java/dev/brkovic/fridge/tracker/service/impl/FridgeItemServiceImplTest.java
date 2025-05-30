package dev.brkovic.fridge.tracker.service.impl;

import dev.brkovic.fridge.api.model.AddFridgeItemRequest;
import dev.brkovic.fridge.api.model.FridgeItemResponse;
import dev.brkovic.fridge.tracker.dto.FridgeItemDTO;
import dev.brkovic.fridge.tracker.entity.FridgeEntity;
import dev.brkovic.fridge.tracker.entity.FridgeItemEntity;
import dev.brkovic.fridge.tracker.entity.ItemEntity;
import dev.brkovic.fridge.tracker.entity.UserEntity;
import dev.brkovic.fridge.tracker.exception.InternalException;
import dev.brkovic.fridge.tracker.mapper.FridgeItemMapper;
import dev.brkovic.fridge.tracker.repository.FridgeItemRepository;
import dev.brkovic.fridge.tracker.repository.FridgeRepository;
import dev.brkovic.fridge.tracker.repository.ItemRepository;
import dev.brkovic.fridge.tracker.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FridgeItemServiceImplTest {
    @InjectMocks
    private FridgeItemServiceImpl fridgeItemService;

    @Mock
    private FridgeRepository fridgeRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private FridgeItemRepository fridgeItemRepository;

    @Mock
    private FridgeItemMapper fridgeItemMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    private static final String USER_ID = "user1";
    private static final String USERNAME = "testUser";
    private static final String FRIDGE_ID = "fridge1";
    private static final String ITEM_ID = "item1";
    private static final String FRIDGE_ITEM_ID = "fridgeItem1";
    private static final String ITEM_NAME = "Test Item";

    private UserEntity testUser;

    @BeforeEach
    void setUp() {
        testUser = new UserEntity();
        testUser.setId(USER_ID);
        testUser.setUsername(USERNAME);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(USERNAME);
        SecurityContextHolder.setContext(securityContext);
        when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(testUser));

        ReflectionTestUtils.setField(fridgeItemService, "userRepository", userRepository);
    }

    @Test
    void addItemToFridge_WhenValidRequest_ThenSuccess() {
        // given
        AddFridgeItemRequest request = new AddFridgeItemRequest()
                .itemId(ITEM_ID)
                .storedAt(LocalDate.from(LocalDateTime.now()))
                .bestBeforeDate(LocalDate.from(LocalDateTime.now().plusDays(7)));

        FridgeEntity testFridge = new FridgeEntity();
        testFridge.setId(FRIDGE_ID);
        testFridge.setName("Test Fridge");
        testFridge.setUserId(USER_ID);

        ItemEntity testItem = new ItemEntity();
        testItem.setId(ITEM_ID);
        testItem.setName(ITEM_NAME);
        testItem.setUserId(USER_ID);

        FridgeItemEntity testFridgeItem = new FridgeItemEntity();
        testFridgeItem.setId(FRIDGE_ITEM_ID);
        testFridgeItem.setFridgeId(FRIDGE_ID);
        testFridgeItem.setItemId(ITEM_ID);
        testFridgeItem.setStoredAt(LocalDate.from(LocalDateTime.now()));
        testFridgeItem.setBestBeforeDate(LocalDate.from(LocalDateTime.now().plusDays(7)));

        FridgeItemResponse testFridgeItemResponse = new FridgeItemResponse();
        testFridgeItemResponse.setId(FRIDGE_ITEM_ID);
        testFridgeItemResponse.setItemId(ITEM_ID);
        testFridgeItemResponse.setItemName(ITEM_NAME);
        testFridgeItemResponse.setStoredAt(testFridgeItem.getStoredAt());
        testFridgeItemResponse.setBestBeforeDate(testFridgeItem.getBestBeforeDate());

        // when
        when(fridgeRepository.findByIdAndUserId(FRIDGE_ID, USER_ID)).thenReturn(Optional.of(testFridge));
        when(itemRepository.findByIdAndUserId(ITEM_ID, USER_ID)).thenReturn(Optional.of(testItem));
        when(fridgeItemRepository.saveAndFlush(any(FridgeItemEntity.class))).thenReturn(testFridgeItem);
        when(fridgeItemMapper.fridgeItemEntityToFridgeItemResponse(any(), anyString())).thenReturn(testFridgeItemResponse);

        FridgeItemResponse response = fridgeItemService.addItemToFridge(FRIDGE_ID, request);

        // then
        assertNotNull(response);
        assertEquals(FRIDGE_ITEM_ID, response.getId());
        assertEquals(ITEM_ID, response.getItemId());
        assertEquals(ITEM_NAME, response.getItemName());
        verify(fridgeItemRepository).saveAndFlush(any(FridgeItemEntity.class));
    }

    @Test
    void addItemToFridge_WhenFridgeNotFound_ThenThrowException() {
        // given
        AddFridgeItemRequest request = new AddFridgeItemRequest()
                .itemId(ITEM_ID)
                .storedAt(LocalDate.from(LocalDateTime.now()))
                .bestBeforeDate(LocalDate.from(LocalDateTime.now().plusDays(7)));

        // when
        when(fridgeRepository.findByIdAndUserId(FRIDGE_ID, USER_ID)).thenReturn(Optional.empty());

        // then
        assertThrows(InternalException.class, () -> fridgeItemService.addItemToFridge(FRIDGE_ID, request));
        verify(fridgeItemRepository, never()).saveAndFlush(any());
    }

    @Test
    void getFridgeItems_WhenFridgeExists_ThenSuccess() {
        // given
        FridgeEntity testFridge = new FridgeEntity();
        testFridge.setId(FRIDGE_ID);
        testFridge.setName("Test Fridge");
        testFridge.setUserId(USER_ID);

        FridgeItemDTO fridgeItemDTO = new FridgeItemDTOImpl(FRIDGE_ITEM_ID, FRIDGE_ID, ITEM_ID,
                LocalDate.now(), LocalDate.now().plusDays(7), ITEM_NAME);

        FridgeItemResponse testFridgeItemResponse = new FridgeItemResponse();
        testFridgeItemResponse.setId(FRIDGE_ITEM_ID);
        testFridgeItemResponse.setItemId(ITEM_ID);
        testFridgeItemResponse.setItemName(ITEM_NAME);
        testFridgeItemResponse.setStoredAt(LocalDate.now());
        testFridgeItemResponse.setBestBeforeDate(LocalDate.now().plusDays(7));

        // when
        when(fridgeRepository.findByIdAndUserId(FRIDGE_ID, USER_ID)).thenReturn(Optional.of(testFridge));
        when(fridgeItemRepository.findByFridgeId(FRIDGE_ID)).thenReturn(List.of(fridgeItemDTO));
        when(fridgeItemMapper.fridgeItemDTOToFridgeItemResponse(fridgeItemDTO)).thenReturn(testFridgeItemResponse);

        List<FridgeItemResponse> response = fridgeItemService.getFridgeItems(FRIDGE_ID);

        // then
        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals(FRIDGE_ITEM_ID, response.getFirst().getId());
        assertEquals(ITEM_NAME, response.getFirst().getItemName());
    }

    @Test
    void getFridgeItems_WhenFridgeNotFound_ThenThrowException() {
        // when
        when(fridgeRepository.findByIdAndUserId(FRIDGE_ID, USER_ID)).thenReturn(Optional.empty());

        // then
        assertThrows(InternalException.class, () -> fridgeItemService.getFridgeItems(FRIDGE_ID));
        verify(fridgeItemRepository, never()).findByFridgeId(any());
    }

    @Test
    void removeItemFromFridge_WhenItemExists_ThenSuccess() {
        // given
        FridgeEntity testFridge = new FridgeEntity();
        testFridge.setId(FRIDGE_ID);
        testFridge.setName("Test Fridge");
        testFridge.setUserId(USER_ID);

        FridgeItemDTO fridgeItemDTO = new FridgeItemDTOImpl(FRIDGE_ITEM_ID, FRIDGE_ID, ITEM_ID,
                LocalDate.now(), LocalDate.now().plusDays(7), ITEM_NAME);

        // when
        when(fridgeRepository.findByIdAndUserId(FRIDGE_ID, USER_ID)).thenReturn(Optional.of(testFridge));
        when(fridgeItemRepository.findByFridgeItemId(FRIDGE_ITEM_ID)).thenReturn(fridgeItemDTO);

        fridgeItemService.removeItemFromFridge(FRIDGE_ID, FRIDGE_ITEM_ID);

        // then
        verify(fridgeItemRepository).deleteFridgeItem(FRIDGE_ITEM_ID);
    }

    @Test
    void removeItemFromFridge_WhenItemNotFound_ThenThrowException() {
        // when
        when(fridgeItemRepository.findByFridgeItemId(FRIDGE_ITEM_ID)).thenReturn(null);

        // then
        assertThrows(InternalException.class, () -> fridgeItemService.removeItemFromFridge(FRIDGE_ID, FRIDGE_ITEM_ID));
        verify(fridgeItemRepository, never()).deleteFridgeItem(any());
    }

    private static final class FridgeItemDTOImpl implements FridgeItemDTO {
        private final String id;
        private final String fridgeId;
        private final String itemId;
        private final LocalDate storedAt;
        private final LocalDate bestBeforeDate;
        private final String itemName;

        public FridgeItemDTOImpl(String id, String fridgeId, String itemId, LocalDate storedAt,
                          LocalDate bestBeforeDate, String itemName) {
            this.id = id;
            this.fridgeId = fridgeId;
            this.itemId = itemId;
            this.storedAt = storedAt;
            this.bestBeforeDate = bestBeforeDate;
            this.itemName = itemName;
        }

        @Override
        public String getId() {
            return id;
        }

        @Override
        public String getFridgeId() {
            return fridgeId;
        }

        @Override
        public String getItemId() {
            return itemId;
        }

        @Override
        public LocalDate getStoredAt() {
            return storedAt;
        }

        @Override
        public LocalDate getBestBeforeDate() {
            return bestBeforeDate;
        }

        @Override
        public String getItemName() {
            return itemName;
        }
    }
}