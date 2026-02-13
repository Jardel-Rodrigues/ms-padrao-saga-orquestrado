package com.br.softstream.inventory_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.br.softstream.inventory_service.model.OrderInventory;

public interface OrderInventoryRepository extends JpaRepository<OrderInventory, Integer> {

	Boolean existsByOrderIdAndTransactionId(String orderId, String transactionId);
	List<OrderInventory> findByOrderIdAndTransactionId(String orderId, String transactionId);
	
}
