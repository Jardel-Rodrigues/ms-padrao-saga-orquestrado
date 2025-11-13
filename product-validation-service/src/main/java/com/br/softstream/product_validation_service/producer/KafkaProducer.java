package com.br.softstream.product_validation_service.producer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaProducer {

	private final KafkaTemplate<String, String> kafkaTemplete;
	
	@Value("${spring.kafka.topic.orchestrator}")
	private String orchestratorTopic;
	
	public void sendEvent(String payload) {
		try {
			log.info("Enviando evento para o topico {} com os dados {}", orchestratorTopic, payload);
			kafkaTemplete.send(orchestratorTopic, payload);
		} catch (Exception ex) {
			log.error("Erro ao tentar enviar dados para o topico {} com os valores {}", orchestratorTopic, payload, ex);
		}
	}
	
}
