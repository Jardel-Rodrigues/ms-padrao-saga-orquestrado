package com.br.softstream.orchestrator_service.producer;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class SagaOrchestratorProducer {

	private final KafkaTemplate<String, String> kafkaTemplete;
	
	public void sendEvent(String payload, String topic) {
		try {
			log.info("Enviando evento para o topico {} com os dados {}", topic, payload);
			kafkaTemplete.send(topic, payload);
		} catch (Exception ex) {
			log.error("Erro ao tentar enviar dados para o topico {} com os valores {}", topic, payload, ex);
		}
	}
	
}
