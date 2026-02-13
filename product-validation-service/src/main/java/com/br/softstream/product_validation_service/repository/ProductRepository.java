package com.br.softstream.product_validation_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.br.softstream.product_validation_service.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

	Boolean existsByCode(String code);
	
}
