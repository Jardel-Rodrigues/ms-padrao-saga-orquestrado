package com.br.softstream.orderservice.service;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.util.ObjectUtils.isEmpty;
import org.springframework.stereotype.Service;

import com.br.softstream.orderservice.config.exception.ValidationException;
import com.br.softstream.orderservice.document.Event;
import com.br.softstream.orderservice.dto.EventFiltersDTO;
import com.br.softstream.orderservice.repository.EventRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class EventService {
	
	private final EventRepository repository;
	
	public void notifyEnding(Event event) {
		event.setOrderId(event.getOrderId());
		event.setCreatedAt(LocalDateTime.now());
		save(event);
		log.info("Order {} with saga notifid! TransactioId: {}", event.getOrderId(), event.getTransactionId());
	}
	
	public List<Event> findAll() {
		return repository.findAllByOrderByCreatedAtDesc();
	}
	
	public Event findByFilters(EventFiltersDTO filters) {
		validateEmptyFilters(filters);
		if(!isEmpty(filters.getOrderId())) {
			return findByOrderId(filters.getOrderId());
		} else {
			return findByTransactionId(filters.getTransactionId());
		}
	}
	
	private Event findByOrderId(String orderId) {
		return repository.findByOrderIdOrderByCreatedAtDesc(orderId)
				.orElseThrow(() -> new ValidationException("Event not found by orderID."));
	}
	
	private Event findByTransactionId(String transactionId) {
		return repository.findByTransactionIdOrderByCreatedAtDesc(transactionId)
				.orElseThrow(() -> new ValidationException("Event not found by transactionID."));
	}
	
	private void validateEmptyFilters(EventFiltersDTO filters) {
		if(isEmpty(filters.getOrderId()) && isEmpty(filters.getTransactionId())) {
			throw new ValidationException("OrderID or TransactionID must be informed.");
		}
	}
	
	public Event save(Event event) {
		return repository.save(event);
	}

}
