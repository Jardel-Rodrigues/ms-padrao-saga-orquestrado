package com.br.softstream.payment_service.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.br.softstream.payment_service.service.PaymentService;
import com.br.softstream.payment_service.utils.JsonUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentConsumer {

	private final JsonUtil jsonUtil;
	private final PaymentService paymentService;
	
	@KafkaListener(groupId = "${spring.kafka.consumer.group-id}", topics = "${spring.kafka.topic.payment-success}")
	public void consumerSuccessEvent(String payload) {
		log.info("Recebendo evento de sucesso {} para o topico payment-success", payload);
		var event = jsonUtil.toEvent(payload);
		paymentService.realizePayment(event);
	}
	
	@KafkaListener(groupId = "${spring.kafka.consumer.group-id}", topics = "${spring.kafka.topic.payment-fail}")
	public void consumerFailEvent(String payload) {
		log.info("Recebendo evento de falha {} para o topico payment-fail", payload);
		var event = jsonUtil.toEvent(payload);
		paymentService.realizeRefund(event);
	}
}
