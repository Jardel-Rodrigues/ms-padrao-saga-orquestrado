package com.br.softstream.inventory_service.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.br.softstream.inventory_service.service.InventoryService;
import com.br.softstream.inventory_service.utils.JsonUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class InventoryConsumer {

	private final JsonUtil jsonUtil;
	private final InventoryService inventoryService;
	
	@KafkaListener(groupId = "${spring.kafka.consumer.group-id}", topics = "${spring.kafka.topic.inventory-success}")
	public void consumerSuccessEvent(String payload) {
		log.info("Recebendo evento de sucesso {} para o topico inventory-success", payload);
		var event = jsonUtil.toEvent(payload);
		inventoryService.updateInventory(event);
	}
	
	@KafkaListener(groupId = "${spring.kafka.consumer.group-id}", topics = "${spring.kafka.topic.inventory-fail}")
	public void consumerFailEvent(String payload) {
		log.info("Recebendo evento de falha {} para o topico inventory-fail", payload);
		var event = jsonUtil.toEvent(payload);
		inventoryService.rollbackInventory(event);
	}
}
