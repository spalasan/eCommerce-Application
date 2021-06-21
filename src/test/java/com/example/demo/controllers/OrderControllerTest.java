package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderControllerTest {

    private OrderController orderController;

    private final UserRepository userRepo = mock(UserRepository.class);

    private final OrderRepository orderRepo = mock(OrderRepository.class);


    @Before
    public void setUp() {
        orderController = new OrderController();
        TestUtils.injectObjects(orderController, "userRepository", userRepo);
        TestUtils.injectObjects(orderController, "orderRepository", orderRepo);
    }

    @Test
    public void verify_submit() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testUser");
        Item item = new Item();
        item.setId(1L);
        item.setName("item1");
        item.setPrice(new BigDecimal(15));
        List<Item> items = new ArrayList<>();
        items.add(item);
        Cart cart = new Cart();
        cart.setItems(items);
        user.setCart(cart);
        when(userRepo.findByUsername(anyString())).thenReturn(user);

        final ResponseEntity<UserOrder> response = orderController.submit(user.getUsername());
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        UserOrder userOrder = response.getBody();
        assertNotNull(userOrder);
        assertEquals(1L, userOrder.getItems().size());
        assertEquals(item.getName(), userOrder.getItems().get(0).getName());
    }

    @Test
    public void verify_submit_when_user_not_found() {
        final ResponseEntity<UserOrder> response = orderController.submit("testUser");
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void verify_get_orders_for_user() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testUser");
        Item item = new Item();
        item.setId(1L);
        item.setName("item1");
        item.setPrice(new BigDecimal(15));
        List<Item> items = new ArrayList<>();
        items.add(item);
        Cart cart = new Cart();
        cart.setItems(items);
        user.setCart(cart);
        when(userRepo.findByUsername(anyString())).thenReturn(user);
        List<UserOrder> orders = new ArrayList<>();
        UserOrder order = new UserOrder();
        order.setItems(cart.getItems());
        orders.add(order);
        when(orderRepo.findByUser(any())).thenReturn(orders);

        final ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser(user.getUsername());
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        List<UserOrder> userOrders = response.getBody();
        assertNotNull(userOrders);
        assertEquals(1L, userOrders.size());
        assertEquals(item.getName(), userOrders.get(0).getItems().get(0).getName());
    }

    @Test
    public void verify_get_orders_for_user_when_user_not_found() {
        final ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("testUser");
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
