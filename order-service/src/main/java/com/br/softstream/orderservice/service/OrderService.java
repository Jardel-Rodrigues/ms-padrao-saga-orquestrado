package com.br.softstream.orderservice.service;

import static java.time.Instant.now;
import java.time.LocalDateTime;
import static java.util.UUID.randomUUID;
import static java.lang.String.format;
import org.springframework.stereotype.Service;

import com.br.softstream.orderservice.document.Event;
import com.br.softstream.orderservice.document.Order;
import com.br.softstream.orderservice.dto.OrderRequestDTO;
import com.br.softstream.orderservice.producer.SagaProducer;
import com.br.softstream.orderservice.repository.OrderRepository;
import com.br.softstream.orderservice.utils.JsonUtil;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class OrderService {
	
	private static final String TRANSACTION_ID_PATTERN = "%s_%s";
	
	private final OrderRepository repository;
	
	private final EventService eventService;
	private final SagaProducer producer;
	private final JsonUtil jsonUtil;
	
	public Order createOrder(OrderRequestDTO orderRequest) {
		var order = Order.builder()
				.products(orderRequest.getProducts())
				.createdAt(LocalDateTime.now())
				.transactionId(format(TRANSACTION_ID_PATTERN, now().toEpochMilli(), randomUUID()))
				.build();
		
		repository.save(order);
		producer.sendEvent(jsonUtil.toJson(createPayload(order)));
		return order;
		
	}
	
	private Event createPayload(Order order) {
		var event = Event.builder()
				.orderId(order.getId())
				.transactionId(order.getTransactionId())
				.payload(order)
				.createdAt(LocalDateTime.now())
				.build();
		return eventService.save(event);
	}

}
