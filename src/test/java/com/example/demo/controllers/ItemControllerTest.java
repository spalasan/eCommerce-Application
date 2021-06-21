package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemControllerTest {

    private ItemController itemController;

    private final ItemRepository itemRepo = mock(ItemRepository.class);

    @Before
    public void setUp() {
        itemController = new ItemController();
        TestUtils.injectObjects(itemController, "itemRepository", itemRepo);
    }

    @Test
    public void verify_get_all_items() {
        final ResponseEntity<List<Item>> response = itemController.getItems();
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        List<Item> responseItems = response.getBody();
        assertNotNull(responseItems);
        assertEquals(0L, responseItems.size());
    }

    @Test
    public void verify_get_item_by_id() {
        Item item =new Item();
        item.setId(1L);
        item.setName("testItem");
        when(itemRepo.findById(anyLong())).thenReturn(Optional.of(item));

        final ResponseEntity<Item> response = itemController.getItemById(1L);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Item resultItem = response.getBody();
        assertNotNull(resultItem);
        assertEquals(item.getId(), resultItem.getId());
        assertEquals(item.getName(), resultItem.getName());

    }

    @Test
    public void verify_get_items_by_name() {
        Item item =new Item();
        item.setId(1L);
        item.setName("testItem");
        List<Item> items = new ArrayList<>();
        items.add(item);
        when(itemRepo.findByName(anyString())).thenReturn(items);

        final ResponseEntity<List<Item>> response = itemController.getItemsByName(item.getName());
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        List<Item> responseItems = response.getBody();
        assertNotNull(responseItems);
        assertEquals(1L, responseItems.size());
        assertEquals(item.getName(), responseItems.get(0).getName());
    }
}
