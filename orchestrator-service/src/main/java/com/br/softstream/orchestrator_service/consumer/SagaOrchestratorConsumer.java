package com.br.softstream.orchestrator_service.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.br.softstream.orchestrator_service.service.OrchestratorService;
import com.br.softstream.orchestrator_service.utils.JsonUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class SagaOrchestratorConsumer {

	private final JsonUtil jsonUtil;
	private final OrchestratorService orchestratorService;
	
	@KafkaListener(groupId = "${spring.kafka.consumer.group-id}", topics = "${spring.kafka.topic.start-saga}")
	public void consumerStartSagaEvent(String payload) {
		log.info("Recebendo evento {} para o topico start-saga", payload);
		var event = jsonUtil.toEvent(payload);
		orchestratorService.startSaga(event);
	}
		
	@KafkaListener(groupId = "${spring.kafka.consumer.group-id}", topics = "${spring.kafka.topic.start-saga}")
	public void consumerOrchestratorEvent(String payload) {
		log.info("Recebendo evento {} para o topico orchestrator", payload);
		var event = jsonUtil.toEvent(payload);
		orchestratorService.continueSaga(event);
	}
		
	@KafkaListener(groupId = "${spring.kafka.consumer.group-id}", topics = "${spring.kafka.topic.start-saga}")
	public void consumerFinishSuccessEvent(String payload) {
		log.info("Recebendo evento {} para o topico finish-success", payload);
		var event = jsonUtil.toEvent(payload);
		orchestratorService.finishSagaSuccess(event);
	}
		
	@KafkaListener(groupId = "${spring.kafka.consumer.group-id}", topics = "${spring.kafka.topic.start-saga}")
	public void consumerFinishFailEvent(String payload) {
		log.info("Recebendo evento {} para o topico finish-fail", payload);
		var event = jsonUtil.toEvent(payload);
		orchestratorService.finishSagaFail(event);
	}
}
