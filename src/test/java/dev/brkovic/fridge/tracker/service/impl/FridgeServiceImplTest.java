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
class FridgeServiceImplTest {

    @InjectMocks
    private FridgeServiceImpl fridgeService;

    @Mock
    private FridgeRepository fridgeRepository;

    @Mock
    private FridgeItemRepository fridgeItemRepository;

    @Mock
    private FridgeMapper fridgeMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    private static final String USER_ID = "user1";
    private static final String USERNAME = "testUser";
    private static final String FRIDGE_ID = "fridge1";
    private static final String FRIDGE_NAME = "Test Fridge";

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

        ReflectionTestUtils.setField(fridgeService, "userRepository", userRepository);
    }

    @Test
    void createFridge_WhenValidRequest_ThenSuccess() {
        // given
        CreateFridgeRequest request = new CreateFridgeRequest().name(FRIDGE_NAME);
        FridgeEntity testFridge = new FridgeEntity();
        testFridge.setId(FRIDGE_ID);
        testFridge.setName(FRIDGE_NAME);
        testFridge.setUserId(USER_ID);

        FridgeResponse testFridgeResponse = new FridgeResponse();
        testFridgeResponse.setId(FRIDGE_ID);
        testFridgeResponse.setName(FRIDGE_NAME);

        // when
        when(fridgeRepository.saveAndFlush(any(FridgeEntity.class))).thenReturn(testFridge);
        when(fridgeMapper.fridgeEntityToFridgeResponse(testFridge)).thenReturn(testFridgeResponse);

        FridgeResponse response = fridgeService.createFridge(request);

        // then
        assertNotNull(response);
        assertEquals(FRIDGE_ID, response.getId());
        assertEquals(FRIDGE_NAME, response.getName());
        verify(fridgeRepository).saveAndFlush(any(FridgeEntity.class));
    }

    @Test
    void getFridge_WhenFridgeExists_ThenSuccess() {
        // given
        FridgeEntity testFridge = new FridgeEntity();
        testFridge.setId(FRIDGE_ID);
        testFridge.setName(FRIDGE_NAME);
        testFridge.setUserId(USER_ID);

        FridgeResponse testFridgeResponse = new FridgeResponse();
        testFridgeResponse.setId(FRIDGE_ID);
        testFridgeResponse.setName(FRIDGE_NAME);

        // when
        when(fridgeRepository.findByIdAndUserId(FRIDGE_ID, USER_ID)).thenReturn(Optional.of(testFridge));
        when(fridgeMapper.fridgeEntityToFridgeResponse(testFridge)).thenReturn(testFridgeResponse);

        FridgeResponse response = fridgeService.getFridge(FRIDGE_ID);

        // then
        assertNotNull(response);
        assertEquals(FRIDGE_ID, response.getId());
        assertEquals(FRIDGE_NAME, response.getName());
    }

    @Test
    void getFridge_WhenFridgeDoesNotExist_ThenThrowException() {
        // when
        when(fridgeRepository.findByIdAndUserId(FRIDGE_ID, USER_ID)).thenReturn(Optional.empty());

        // then
        assertThrows(InternalException.class, () -> fridgeService.getFridge(FRIDGE_ID));
    }

    @Test
    void getAllFridges_ThenSuccess() {
        // given
        FridgeEntity testFridge = new FridgeEntity();
        testFridge.setId(FRIDGE_ID);
        testFridge.setName(FRIDGE_NAME);
        testFridge.setUserId(USER_ID);

        FridgeEntity fridge2 = new FridgeEntity();
        fridge2.setId("fridge2");
        fridge2.setName("Test Fridge 2");
        fridge2.setUserId(USER_ID);

        FridgeResponse testFridgeResponse = new FridgeResponse();
        testFridgeResponse.setId(FRIDGE_ID);
        testFridgeResponse.setName(FRIDGE_NAME);

        FridgeResponse fridgeResponse2 = new FridgeResponse();
        fridgeResponse2.setId("fridge2");
        fridgeResponse2.setName("Test Fridge 2");

        List<FridgeEntity> fridges = Arrays.asList(testFridge, fridge2);

        // when
        when(fridgeRepository.findByUserId(USER_ID)).thenReturn(fridges);
        when(fridgeMapper.fridgeEntityToFridgeResponse(testFridge)).thenReturn(testFridgeResponse);
        when(fridgeMapper.fridgeEntityToFridgeResponse(fridge2)).thenReturn(fridgeResponse2);

        List<FridgeResponse> response = fridgeService.getAllFridges();

        // then
        assertNotNull(response);
        assertEquals(2, response.size());
        assertEquals(FRIDGE_NAME, response.getFirst().getName());
        assertEquals("Test Fridge 2", response.get(1).getName());
    }

    @Test
    void updateFridge_WhenFridgeExists_ThenSuccess() {
        // given
        UpdateFridgeRequest request = new UpdateFridgeRequest().name("Updated Fridge");
        
        FridgeEntity testFridge = new FridgeEntity();
        testFridge.setId(FRIDGE_ID);
        testFridge.setName(FRIDGE_NAME);
        testFridge.setUserId(USER_ID);

        FridgeEntity updatedFridge = new FridgeEntity();
        updatedFridge.setId(FRIDGE_ID);
        updatedFridge.setName("Updated Fridge");
        updatedFridge.setUserId(USER_ID);

        FridgeResponse updatedResponse = new FridgeResponse();
        updatedResponse.setId(FRIDGE_ID);
        updatedResponse.setName("Updated Fridge");

        // when
        when(fridgeRepository.findByIdAndUserId(FRIDGE_ID, USER_ID)).thenReturn(Optional.of(testFridge));
        when(fridgeRepository.save(any(FridgeEntity.class))).thenReturn(updatedFridge);
        when(fridgeMapper.fridgeEntityToFridgeResponse(any(FridgeEntity.class))).thenReturn(updatedResponse);

        FridgeResponse response = fridgeService.updateFridge(FRIDGE_ID, request);

        // then
        assertNotNull(response);
        assertEquals(FRIDGE_ID, response.getId());
        assertEquals("Updated Fridge", response.getName());
        verify(fridgeRepository).save(any(FridgeEntity.class));
    }

    @Test
    void deleteFridge_WhenFridgeExistsAndEmpty_ThenSuccess() {
        // given
        FridgeEntity testFridge = new FridgeEntity();
        testFridge.setId(FRIDGE_ID);
        testFridge.setName(FRIDGE_NAME);
        testFridge.setUserId(USER_ID);

        // when
        when(fridgeRepository.findByIdAndUserId(FRIDGE_ID, USER_ID)).thenReturn(Optional.of(testFridge));
        when(fridgeItemRepository.countItemsInFridge(FRIDGE_ID)).thenReturn(0);

        fridgeService.deleteFridge(FRIDGE_ID);

        // then
        verify(fridgeRepository).delete(testFridge);
    }

    @Test
    void deleteFridge_WhenFridgeHasItems_ThenThrowException() {
        // given
        FridgeEntity testFridge = new FridgeEntity();
        testFridge.setId(FRIDGE_ID);
        testFridge.setName(FRIDGE_NAME);
        testFridge.setUserId(USER_ID);

        // when
        when(fridgeRepository.findByIdAndUserId(FRIDGE_ID, USER_ID)).thenReturn(Optional.of(testFridge));
        when(fridgeItemRepository.countItemsInFridge(FRIDGE_ID)).thenReturn(1);

        // then
        assertThrows(InternalException.class, () -> fridgeService.deleteFridge(FRIDGE_ID));
        verify(fridgeRepository, never()).delete(any());
    }
}