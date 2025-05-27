package dev.brkovic.fridge.tracker.mapper;

import dev.brkovic.fridge.api.model.ItemResponse;
import dev.brkovic.fridge.tracker.entity.ItemEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.ZoneId;

@Mapper(componentModel = "spring", imports = {ZoneId.class})
public interface ItemMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(target = "createdOn", expression = "java(itemEntity.getCreatedOn().toInstant().atZone(ZoneId.systemDefault()).toOffsetDateTime())")
    ItemResponse itemEntityToItemResponse(ItemEntity itemEntity);
}
