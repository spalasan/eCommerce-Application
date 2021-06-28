package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.exceptions.ExceptionsConstants;
import com.example.demo.exceptions.NotFoundException;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CartControllerTest {

    private CartController cartController;

    private final UserRepository userRepo = mock(UserRepository.class);

    private final CartRepository cartRepo = mock(CartRepository.class);

    private final ItemRepository itemRepo = mock(ItemRepository.class);

    @Before
    public void setUp() {
        cartController = new CartController();
        TestUtils.injectObjects(cartController, "userRepository", userRepo);
        TestUtils.injectObjects(cartController, "cartRepository", cartRepo);
        TestUtils.injectObjects(cartController, "itemRepository", itemRepo);
    }

    @Test
    public void verify_add_to_cart() {
        BigDecimal price = new BigDecimal(15);
        Item item = new Item();
        item.setId(1L);
        item.setName("item1");
        item.setPrice(price);
        User u = new User();
        u.setId(1L);
        u.setUsername("testUser");
        u.setCart(new Cart());
        when(userRepo.findByUsername(anyString())).thenReturn(u);
        when(itemRepo.findById(anyLong())).thenReturn(Optional.of(item));
        ModifyCartRequest request = new ModifyCartRequest();
        request.setItemId(1L);
        request.setQuantity(1);
        request.setUsername("testUser");

        final ResponseEntity<Cart> response = cartController.addTocart(request);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Cart cart = response.getBody();
        assertNotNull(cart);
        assertEquals(1, cart.getItems().size());
        assertEquals(price, cart.getTotal());
        assertEquals("item1", cart.getItems().get(0).getName());

    }

    @Test
    public void verify_add_to_cart_when_user_not_found() {
        ModifyCartRequest request = new ModifyCartRequest();
        request.setItemId(1L);
        request.setQuantity(1);
        request.setUsername("testUser");

        Exception exception = assertThrows(NotFoundException.class, () -> cartController.addTocart(request));
        assertEquals(ExceptionsConstants.USER_NOT_FOUND, exception.getMessage());
    }

    @Test
    public void verify_add_to_cart_when_item_not_found() {
        when(userRepo.findByUsername(anyString())).thenReturn(new User());
        ModifyCartRequest request = new ModifyCartRequest();
        request.setItemId(1L);
        request.setQuantity(1);
        request.setUsername("testUser");

        Exception exception = assertThrows(NotFoundException.class, () -> cartController.addTocart(request));
        assertEquals(ExceptionsConstants.ITEM_NOT_FOUND, exception.getMessage());
    }

    @Test
    public void verify_remove_from_cart() {
        BigDecimal price = new BigDecimal(15);
        Cart c = new Cart();
        Item item = new Item();
        item.setId(1L);
        item.setName("item1");
        item.setPrice(price);
        List<Item> items = new ArrayList<>();
        items.add(item);
        items.add(item);
        c.setItems(items);
        c.setTotal(price.multiply(new BigDecimal(2)));
        User u = new User();
        u.setId(1L);
        u.setUsername("testUser");
        u.setCart(c);
        when(userRepo.findByUsername(anyString())).thenReturn(u);
        when(itemRepo.findById(anyLong())).thenReturn(Optional.of(item));
        ModifyCartRequest request = new ModifyCartRequest();
        request.setItemId(1L);
        request.setQuantity(1);
        request.setUsername("testUser");

        final ResponseEntity<Cart> response = cartController.removeFromcart(request);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Cart cart = response.getBody();
        assertNotNull(cart);
        assertEquals(1, cart.getItems().size());
        assertEquals(price, cart.getTotal());
    }

    @Test
    public void verify_remove_from_cart_when_user_not_found() {
        ModifyCartRequest request = new ModifyCartRequest();
        request.setItemId(1L);
        request.setQuantity(1);
        request.setUsername("testUser");

        Exception exception = assertThrows(NotFoundException.class, () -> cartController.removeFromcart(request));
        assertEquals(ExceptionsConstants.USER_NOT_FOUND, exception.getMessage());
    }

    @Test
    public void verify_remove_from_cart_when_item_not_found() {
        when(userRepo.findByUsername(anyString())).thenReturn(new User());
        ModifyCartRequest request = new ModifyCartRequest();
        request.setItemId(1L);
        request.setQuantity(1);
        request.setUsername("testUser");
        Exception exception = assertThrows(NotFoundException.class, () -> cartController.removeFromcart(request));
        assertEquals(ExceptionsConstants.ITEM_NOT_FOUND, exception.getMessage());

    }

}
