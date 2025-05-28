package dev.brkovic.fridge.tracker.mapper;

import dev.brkovic.fridge.api.model.FridgeItemResponse;
import dev.brkovic.fridge.tracker.dto.FridgeItemDTO;
import dev.brkovic.fridge.tracker.entity.FridgeItemEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.ZoneId;

@Mapper(componentModel = "spring", imports = {ZoneId.class})
public interface FridgeItemMapper {
    @Mapping(target = "storedAt", source = "fridgeItemDTO.storedAt")
    @Mapping(target = "bestBeforeDate", source = "fridgeItemDTO.bestBeforeDate")
    FridgeItemResponse fridgeItemDTOToFridgeItemResponse(FridgeItemDTO fridgeItemDTO);

    @Mapping(target = "itemName", source = "itemName")
    @Mapping(target = "storedAt", source = "fridgeItemEntity.storedAt")
    @Mapping(target = "bestBeforeDate", source = "fridgeItemEntity.bestBeforeDate")
    FridgeItemResponse fridgeItemEntityToFridgeItemResponse(FridgeItemEntity fridgeItemEntity, String itemName);
}
