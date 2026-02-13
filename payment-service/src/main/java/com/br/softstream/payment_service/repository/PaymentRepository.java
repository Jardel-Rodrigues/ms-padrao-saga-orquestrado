package com.br.softstream.payment_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.br.softstream.payment_service.model.Payment;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {
	
	Boolean existsByOrderIdAndTransactionId(String orderId, String transactionId);
	Optional<Payment> findByOrderIdAndTransactionId(String orderId, String transactionId);

}
