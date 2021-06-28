package com.example.demo.controllers;

import com.example.demo.exceptions.ExceptionsConstants;
import com.example.demo.exceptions.NotFoundException;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/item")
public class ItemController {
	private static Logger log = LoggerFactory.getLogger("splunk.logger");

	@Autowired
	private ItemRepository itemRepository;
	
	@GetMapping
	public ResponseEntity<List<Item>> getItems() {
		return ResponseEntity.ok(itemRepository.findAll());
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Item> getItemById(@PathVariable Long id) {
		return ResponseEntity.of(itemRepository.findById(id));
	}
	
	@GetMapping("/name/{name}")
	public ResponseEntity<List<Item>> getItemsByName(@PathVariable String name) {
		log.debug("Find items by name {}", name);
		List<Item> items = itemRepository.findByName(name);
		if (items == null || items.isEmpty()) {
			log.info("Item Not Found:{}", name);
			throw new NotFoundException(ExceptionsConstants.ITEM_NOT_FOUND);
		}
		return ResponseEntity.ok(items);
			
	}
	
}
