package com.example.demo.controllers;

import com.example.demo.exceptions.ExceptionsConstants;
import com.example.demo.exceptions.NotFoundException;
import com.example.demo.exceptions.PasswordValidationException;
import com.example.demo.exceptions.UserAlreadyExistsException;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

	private static Logger log = LoggerFactory.getLogger("splunk.logger");
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@GetMapping("/id/{id}")
	public ResponseEntity<User> findById(@PathVariable Long id) {
		return ResponseEntity.of(userRepository.findById(id));
	}
	
	@GetMapping("/{username}")
	public ResponseEntity<User> findByUserName(@PathVariable String username) {
		User user = userRepository.findByUsername(username);
		if (user == null) {
			log.error("Could not find user {}", username);
			throw new NotFoundException(ExceptionsConstants.USER_NOT_FOUND);
		}
		return ResponseEntity.ok(user);
	}
	
	@PostMapping("/create")
	public ResponseEntity<User> createUser(@RequestBody CreateUserRequest createUserRequest) {
		User exists = userRepository.findByUsername(createUserRequest.getUsername());
		if (exists != null) {
			log.error("Username {} already exists", createUserRequest.getUsername());
			throw new UserAlreadyExistsException(ExceptionsConstants.USERNAME_ALREADY_EXISTS);
		}
		User user = new User();
		user.setUsername(createUserRequest.getUsername());
		log.debug("User name set with {}", createUserRequest.getUsername());
		Cart cart = new Cart();
		cartRepository.save(cart);
		if(createUserRequest.getPassword().length() < 7 || !createUserRequest.getPassword().equals(createUserRequest.getConfirmPassword())) {
			log.error("Error with user password. Cannot create user {}", createUserRequest.getUsername());
			throw new PasswordValidationException(ExceptionsConstants.INVALID_PASSWORD);
		}
		user.setPassword(bCryptPasswordEncoder.encode(createUserRequest.getPassword()));
		user.setCart(cart);
		userRepository.save(user);
		log.info("Created user {}", user.getUsername());
		return ResponseEntity.ok(user);
	}
	
}
