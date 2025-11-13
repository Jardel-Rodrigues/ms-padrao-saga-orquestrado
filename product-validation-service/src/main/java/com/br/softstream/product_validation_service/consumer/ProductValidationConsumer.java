package com.br.softstream.product_validation_service.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.br.softstream.product_validation_service.utils.JsonUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductValidationConsumer {

	private final JsonUtil jsonUtil;
	
	@KafkaListener(
			groupId = "${spring.kafka.consumer.group-id}",
			topics = "${spring.kafka.topic.product-validation-success}")
	public void consumerSuccessEvent(String payload) {
		log.info("Recebendo evento de sucesso {} para o topico product-validation-success", payload);
		var event = jsonUtil.toEvent(payload);
		log.info(event.toString());
	}
	
	
	@KafkaListener(
			groupId = "${spring.kafka.consumer.group-id}",
			topics = "${spring.kafka.topic.product-validation-fail}")
	public void consumerFailEvent(String payload) {
		log.info("Recebendo evento de falha {} para o topico product-validation-fail", payload);
		var event = jsonUtil.toEvent(payload);
		log.info(event.toString());
	}
}
