package com.br.softstream.orderservice.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.br.softstream.orderservice.document.Event;

@Repository
public interface EventRepository extends MongoRepository<Event, String> {
	
	List<Event> findAllByOrderByCreatedAtDesc();

	Optional<Event> findByOrderIdOrderByCreatedAtDesc(String orderId);
	
	Optional<Event> findByTransactionIdOrderByCreatedAtDesc(String transactionId);
	
}
