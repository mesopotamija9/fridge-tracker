package dev.brkovic.fridge.tracker.mapper;

import dev.brkovic.fridge.api.model.FridgeResponse;
import dev.brkovic.fridge.tracker.entity.FridgeEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.ZoneId;

@Mapper(componentModel = "spring", imports = {ZoneId.class})
public interface FridgeMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(target = "createdOn", expression = "java(fridgeEntity.getCreatedOn().toInstant().atZone(ZoneId.systemDefault()).toOffsetDateTime())")
    FridgeResponse fridgeEntityToFridgeResponse(FridgeEntity fridgeEntity);
}