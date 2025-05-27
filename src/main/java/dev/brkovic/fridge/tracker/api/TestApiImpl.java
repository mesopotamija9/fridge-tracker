package dev.brkovic.fridge.tracker.api;

import dev.brkovic.fridge.api.ItemApi;
import dev.brkovic.fridge.api.model.Item;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TestApiImpl implements ItemApi {
    @Override
    public ResponseEntity<List<Item>> getItems() {
        Item i = new Item();
        i.setId("Test");
        i.setName("Name");
        return ResponseEntity.ok(List.of(i));
    }
}
