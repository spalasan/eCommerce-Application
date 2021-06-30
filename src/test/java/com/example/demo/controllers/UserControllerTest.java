package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.exceptions.ExceptionsConstants;
import com.example.demo.exceptions.NotFoundException;
import com.example.demo.exceptions.PasswordValidationException;
import com.example.demo.exceptions.UserAlreadyExistsException;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {

    private UserController userController;

    private UserRepository userRepository = mock(UserRepository.class);

    private CartRepository cartRepository = mock(CartRepository.class);

    private BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);

    @Before
    public void setUp() {
        userController = new UserController();
        TestUtils.injectObjects(userController, "userRepository", userRepository);
        TestUtils.injectObjects(userController, "cartRepository", cartRepository);
        TestUtils.injectObjects(userController, "bCryptPasswordEncoder", encoder);

    }

    @Test
    public void create_user_happy_path() {
        when(encoder.encode(anyString())).thenReturn("thisIsHashed");
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("test");
        createUserRequest.setPassword("testPassword");
        createUserRequest.setConfirmPassword("testPassword");

        final ResponseEntity<User> response = userController.createUser(createUserRequest);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        User user = response.getBody();
        assertNotNull(user);
        assertEquals(0, user.getId());
        assertEquals("test", user.getUsername());
        assertEquals("thisIsHashed", user.getPassword());

    }

    @Test
    public void verify_find_by_user_name() {
        User u = new User();
        u.setUsername("testUsername");
        when(userRepository.findByUsername(anyString())).thenReturn(u);
        final ResponseEntity<User> response = userController.findByUserName("testUsername");
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        User user = response.getBody();
        assertNotNull(user);
        assertEquals(0, user.getId());
        assertEquals("testUsername", user.getUsername());
    }

    @Test
    public void verify_find_by_id() {
        User u = new User();
        u.setId(1L);
        u.setUsername("testUsername");
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(u));
        final ResponseEntity<User> response = userController.findById(1L);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        User user = response.getBody();
        assertNotNull(user);
        assertEquals(1L, user.getId());
        assertEquals("testUsername", user.getUsername());
    }

    @Test
    public void verify_create_user_when_password_not_valid() {
        when(encoder.encode(anyString())).thenReturn("thisIsHashed");
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("test");
        createUserRequest.setPassword("123456");
        createUserRequest.setConfirmPassword("123456");

        Exception exception = assertThrows(PasswordValidationException.class, () -> userController.createUser(createUserRequest));
        assertEquals("Invalid Password", exception.getMessage());
    }

    @Test
    public void verify_create_user_when_user_already_exists() {
        when(userRepository.findByUsername(anyString())).thenReturn(new User());
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("test");
        createUserRequest.setPassword("1234567");
        createUserRequest.setConfirmPassword("1234567");

        Exception exception = assertThrows(UserAlreadyExistsException.class, () -> userController.createUser(createUserRequest));
        assertEquals(ExceptionsConstants.USERNAME_ALREADY_EXISTS, exception.getMessage());
    }

    @Test
    public void verify_find_by_user_name_when_user_not_found() {
        Exception exception = assertThrows(NotFoundException.class, () -> userController.findByUserName("testUsername"));
        assertEquals(ExceptionsConstants.USER_NOT_FOUND, exception.getMessage());
    }
}
