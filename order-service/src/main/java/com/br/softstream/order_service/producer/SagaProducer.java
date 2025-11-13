package com.br.softstream.order_service.producer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class SagaProducer {

	private final KafkaTemplate<String, String> kafkaTemplete;
	
	@Value("${spring.kafka.topic.start-saga}")
	private String startSagaTopic;
	
	public void sendEvent(String payload) {
		try {
			log.info("Enviando evento para o topico {} com os dados {}", startSagaTopic, payload);
			kafkaTemplete.send(startSagaTopic, payload);
		} catch (Exception ex) {
			log.error("Erro ao tentar enviar dados para o topico {} com os valores {}", startSagaTopic, payload, ex);
		}
	}
	
}
