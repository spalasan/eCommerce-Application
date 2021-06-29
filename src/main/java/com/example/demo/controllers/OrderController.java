package com.example.demo.controllers;

import com.example.demo.exceptions.ExceptionsConstants;
import com.example.demo.exceptions.NotFoundException;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order")
public class OrderController {
	private static Logger log = LoggerFactory.getLogger("splunk.logger");
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private OrderRepository orderRepository;
	
	
	@PostMapping("/submit/{username}")
	public ResponseEntity<UserOrder> submit(@PathVariable String username) {
		log.debug("Submitting order of username {}", username);
		User user = userRepository.findByUsername(username);
		if(user == null) {
			log.error("Could not find user with username {}", username);
			throw new NotFoundException(ExceptionsConstants.USER_NOT_FOUND);
		}
		UserOrder order = UserOrder.createFromCart(user.getCart());
		orderRepository.save(order);
		log.info("Submitted order of user {}", username);
		return ResponseEntity.ok(order);
	}
	
	@GetMapping("/history/{username}")
	public ResponseEntity<List<UserOrder>> getOrdersForUser(@PathVariable String username) {
		log.debug("Get orders for user {}", username);
		User user = userRepository.findByUsername(username);
		if(user == null) {
			log.error("Could not find user with username {}", username);
			throw new NotFoundException(ExceptionsConstants.USER_NOT_FOUND);
		}
		return ResponseEntity.ok(orderRepository.findByUser(user));
	}
}
