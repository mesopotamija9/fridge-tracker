package dev.brkovic.fridge.tracker.mapper;

import dev.brkovic.fridge.api.model.FridgeItemResponse;
import dev.brkovic.fridge.tracker.dto.FridgeItemDTO;
import dev.brkovic.fridge.tracker.entity.FridgeItemEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Date;

@Mapper(componentModel = "spring", imports = {ZoneId.class})
public interface FridgeItemMapper {
    @Mapping(target = "storedAt", expression = "java(toOffsetDateTime(fridgeItemDTO.getStoredAt()))")
    @Mapping(target = "bestBeforeDate", expression = "java(toOffsetDateTime(fridgeItemDTO.getBestBeforeDate()))")
    FridgeItemResponse fridgeItemDTOToFridgeItemResponse(FridgeItemDTO fridgeItemDTO);

    @Mapping(target = "itemName", source = "itemName")
    @Mapping(target = "storedAt", source = "fridgeItemEntity.storedAt")
    @Mapping(target = "bestBeforeDate", source = "fridgeItemEntity.bestBeforeDate")
    FridgeItemResponse fridgeItemEntityToFridgeItemResponse(FridgeItemEntity fridgeItemEntity, String itemName);

    default OffsetDateTime toOffsetDateTime(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toOffsetDateTime();
    }
}
