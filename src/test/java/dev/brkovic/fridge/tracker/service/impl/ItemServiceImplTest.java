package dev.brkovic.fridge.tracker.service.impl;

import dev.brkovic.fridge.api.model.CreateItemRequest;
import dev.brkovic.fridge.api.model.ItemResponse;
import dev.brkovic.fridge.api.model.UpdateItemRequest;
import dev.brkovic.fridge.tracker.entity.ItemEntity;
import dev.brkovic.fridge.tracker.entity.UserEntity;
import dev.brkovic.fridge.tracker.exception.InternalException;
import dev.brkovic.fridge.tracker.mapper.ItemMapper;
import dev.brkovic.fridge.tracker.repository.FridgeItemRepository;
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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {
    @InjectMocks
    private ItemServiceImpl itemService;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private FridgeItemRepository fridgeItemRepository;

    @Mock
    private ItemMapper itemMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    private static final String USER_ID = "user1";
    private static final String USERNAME = "testUser";
    private static final String ITEM_ID = "item1";
    private static final String ITEM_NAME = "Test Item";

    private UserEntity testUser;
    private ItemEntity testItem;
    private ItemResponse testItemResponse;

    @BeforeEach
    void setUp() {
        testUser = new UserEntity();
        testUser.setId(USER_ID);
        testUser.setUsername(USERNAME);

        testItem = new ItemEntity();
        testItem.setId(ITEM_ID);
        testItem.setName(ITEM_NAME);
        testItem.setUserId(USER_ID);

        testItemResponse = new ItemResponse();
        testItemResponse.setId(ITEM_ID);
        testItemResponse.setName(ITEM_NAME);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(USERNAME);
        SecurityContextHolder.setContext(securityContext);
        when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(testUser));

        ReflectionTestUtils.setField(itemService, "userRepository", userRepository);
    }

    @Test
    void createItem_WhenValidRequest_ThenSuccess() {
        // given
        CreateItemRequest request = new CreateItemRequest().name(ITEM_NAME);

        // when
        when(itemRepository.saveAndFlush(any(ItemEntity.class))).thenReturn(testItem);
        when(itemMapper.itemEntityToItemResponse(testItem)).thenReturn(testItemResponse);

        ItemResponse response = itemService.createItem(request);

        // then
        assertNotNull(response);
        assertEquals(ITEM_ID, response.getId());
        assertEquals(ITEM_NAME, response.getName());
        verify(itemRepository).saveAndFlush(any(ItemEntity.class));
    }

    @Test
    void getItem_WhenItemExists_ThenSuccess() {
        // when
        when(itemRepository.findByIdAndUserId(ITEM_ID, USER_ID)).thenReturn(Optional.of(testItem));
        when(itemMapper.itemEntityToItemResponse(testItem)).thenReturn(testItemResponse);

        ItemResponse response = itemService.getItem(ITEM_ID);

        // then
        assertNotNull(response);
        assertEquals(ITEM_ID, response.getId());
        assertEquals(ITEM_NAME, response.getName());
    }

    @Test
    void getItem_WhenItemDoesNotExist_ThenThrowException() {
        // when
        when(itemRepository.findByIdAndUserId(ITEM_ID, USER_ID)).thenReturn(Optional.empty());

        // then
        assertThrows(InternalException.class, () -> itemService.getItem(ITEM_ID));
    }

    @Test
    void getAllItems_ThenSuccess() {
        // given
        ItemEntity item2 = new ItemEntity();
        item2.setId("item2");
        item2.setName("Test Item 2");
        item2.setUserId(USER_ID);

        ItemResponse itemResponse2 = new ItemResponse();
        itemResponse2.setId("item2");
        itemResponse2.setName("Test Item 2");

        List<ItemEntity> items = Arrays.asList(testItem, item2);

        // when
        when(itemRepository.findByUserId(USER_ID)).thenReturn(items);
        when(itemMapper.itemEntityToItemResponse(testItem)).thenReturn(testItemResponse);
        when(itemMapper.itemEntityToItemResponse(item2)).thenReturn(itemResponse2);

        List<ItemResponse> response = itemService.getAllItems();

        // then
        assertNotNull(response);
        assertEquals(2, response.size());
        assertEquals(ITEM_NAME, response.getFirst().getName());
        assertEquals("Test Item 2", response.get(1).getName());
    }

    @Test
    void updateItem_WhenItemExists_ThenSuccess() {
        // given
        UpdateItemRequest request = new UpdateItemRequest().name("Updated Item");
        ItemEntity updatedItem = new ItemEntity();
        updatedItem.setId(ITEM_ID);
        updatedItem.setName("Updated Item");
        updatedItem.setUserId(USER_ID);

        ItemResponse updatedResponse = new ItemResponse();
        updatedResponse.setId(ITEM_ID);
        updatedResponse.setName("Updated Item");

        // when
        when(itemRepository.findByIdAndUserId(ITEM_ID, USER_ID)).thenReturn(Optional.of(testItem));
        when(itemRepository.saveAndFlush(any(ItemEntity.class))).thenReturn(updatedItem);
        when(itemMapper.itemEntityToItemResponse(updatedItem)).thenReturn(updatedResponse);

        ItemResponse response = itemService.updateItem(ITEM_ID, request);

        // then
        assertNotNull(response);
        assertEquals(ITEM_ID, response.getId());
        assertEquals("Updated Item", response.getName());
        verify(itemRepository).saveAndFlush(any(ItemEntity.class));
    }

    @Test
    void deleteItem_WhenItemExistsAndNotInFridge_ThenSuccess() {
        // when
        when(itemRepository.findByIdAndUserId(ITEM_ID, USER_ID)).thenReturn(Optional.of(testItem));
        when(fridgeItemRepository.countFridgesForItemId(ITEM_ID)).thenReturn(0);

        itemService.deleteItem(ITEM_ID);

        // then
        verify(itemRepository).delete(testItem);
    }

    @Test
    void deleteItem_WhenItemInFridge_ThenThrowException() {
        // when
        when(itemRepository.findByIdAndUserId(ITEM_ID, USER_ID)).thenReturn(Optional.of(testItem));
        when(fridgeItemRepository.countFridgesForItemId(ITEM_ID)).thenReturn(1);

        // then
        assertThrows(InternalException.class, () -> itemService.deleteItem(ITEM_ID));
        verify(itemRepository, never()).delete(any());
    }
}