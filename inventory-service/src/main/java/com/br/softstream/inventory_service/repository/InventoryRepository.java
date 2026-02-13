package com.br.softstream.inventory_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.br.softstream.inventory_service.model.Inventory;

public interface InventoryRepository extends JpaRepository<Inventory, Integer> {

	Optional<Inventory> findByProductCode(String productCode);
	
}
