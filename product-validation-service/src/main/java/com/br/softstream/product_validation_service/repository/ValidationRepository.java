package com.br.softstream.product_validation_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.br.softstream.product_validation_service.model.Validation;

@Repository
public interface ValidationRepository extends JpaRepository<Validation, Integer> {

	Boolean existsByOrderIdAndTransactionId(String orderId, String transactionId);
	Optional<Validation> findByOrderIdAndTransactionId(String orderId, String transactionId);
	
}
