package com.br.softstream.inventory_service.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.br.softstream.inventory_service.config.exception.ValidationException;
import com.br.softstream.inventory_service.dto.Event;
import com.br.softstream.inventory_service.dto.History;
import com.br.softstream.inventory_service.dto.Order;
import com.br.softstream.inventory_service.dto.OrderProducts;
import com.br.softstream.inventory_service.enums.ESagaStatus;
import com.br.softstream.inventory_service.model.Inventory;
import com.br.softstream.inventory_service.model.OrderInventory;
import com.br.softstream.inventory_service.producer.KafkaProducer;
import com.br.softstream.inventory_service.repository.InventoryRepository;
import com.br.softstream.inventory_service.repository.OrderInventoryRepository;
import com.br.softstream.inventory_service.utils.JsonUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryService {
	
	private static final String CURRENT_SOURCE = "INVENTORY_SERVICE";
	
	private final JsonUtil jsonUtil;
	private final KafkaProducer producer;
	private final InventoryRepository inventoryRepository;
	private final OrderInventoryRepository orderInventoryRepository;
	
	public void updateInventory(Event event) {
		try {
			checkCurrentValidation(event);
			createOrderInventory(event);
			updateInventory(event.getPayload());
			handleSuccess(event);
		} catch (Exception ex) {
			log.error("Error trying to update inventory:", ex);
			handleFailCurrentNotExecuted(event, ex.getMessage());
		}
		producer.sendEvent(jsonUtil.toJson(event));
	}
	
	public void rollbackInventory(Event event) {
		event.setStatus(ESagaStatus.FAIL);
		event.setSource(CURRENT_SOURCE);
		try
		{
			returnInventoryToPreviousValues(event);
			addHistory(event, "Rollback executed for inventory!");
		} catch (Exception ex) {
			addHistory(event, "Rollback not executed for inventory: ".concat(ex.getMessage()));
		}		
		producer.sendEvent(jsonUtil.toJson(event));
	}

	private void returnInventoryToPreviousValues(Event event) {
		orderInventoryRepository
			.findByOrderIdAndTransactionId(event.getPayload().getId(), event.getTransactionId())
			.forEach(orderInventory -> {
			var inventory = orderInventory.getInventory();
			inventory.setAvailable(orderInventory.getOldQuantity());
			inventoryRepository.save(inventory);
			log.info("Restored inventory for order {} from {} to {} ", event.getPayload().getId(), orderInventory.getNewQuantity(), orderInventory.getOldQuantity());
			});
	}

	private void  checkCurrentValidation(Event event) {
		if (orderInventoryRepository.existsByOrderIdAndTransactionId(event.getPayload().getId(), event.getTransactionId())) {
			throw new ValidationException("There's another transactionId for this validation!");
		}
	}
	
	private void createOrderInventory(Event event) {
		event
		.getPayload()
		.getProducts()
		.forEach(product -> {
			var inventory = findInventoryByProductCode(product.getProduct().getCode());
			var orderInventory = createOrderInventory(event, product, inventory);
			orderInventoryRepository.save(orderInventory);
		});		
	}
	
	private OrderInventory createOrderInventory(Event event, OrderProducts product, Inventory inventory) {
		return OrderInventory.builder()
				.inventory(inventory)
				.oldQuantity(inventory.getAvailable())
				.orderQuantity(product.getQuantity())
				.newQuantity(inventory.getAvailable() - product.getQuantity())
				.orderId(event.getPayload().getId())
				.transactionId(event.getTransactionId())
				.build();
	}
	
	private void updateInventory(Order order) {
		order
		.getProducts()
		.forEach(product -> {
			var inventory = findInventoryByProductCode(product.getProduct().getCode());
			checkInventory(inventory.getAvailable(), product.getQuantity());
			inventory.setAvailable(inventory.getAvailable() - product.getQuantity());
			inventoryRepository.save(inventory);
		});		
	}
	
	private void handleSuccess(Event event) {
		event.setStatus(ESagaStatus.SUCCESS);
		event.setSource(CURRENT_SOURCE);
		addHistory(event, "Inventory updated successfully!");
	}
	
	private void checkInventory(int avaliable, int orderQuantity) {
		if(orderQuantity > avaliable) {
			throw new ValidationException("Product is out of stock!");
		}
		
	}
	
	private void addHistory(Event event, String message) {
		var history = History
				.builder()
				.source(event.getSource())
				.status(event.getStatus())
				.message(message)
				.createdAt(LocalDateTime.now())
				.build();
		event.addToHistory(history);
	}
	
	private void handleFailCurrentNotExecuted(Event event, String message) {
		event.setStatus(ESagaStatus.ROLLBACK_PENDING);
		event.setSource(CURRENT_SOURCE);
		addHistory(event, "Fail to update inventory: ".concat(message));		
	}
	
	private Inventory findInventoryByProductCode(String productCode) {
		return inventoryRepository.findByProductCode(productCode)
				.orElseThrow(() -> new ValidationException("Inventory not found by informed product!"));
	}
	
}
