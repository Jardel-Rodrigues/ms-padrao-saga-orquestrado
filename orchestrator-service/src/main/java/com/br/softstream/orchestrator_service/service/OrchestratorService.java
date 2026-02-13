package com.br.softstream.orchestrator_service.service;

import org.springframework.stereotype.Service;

import com.br.softstream.orchestrator_service.dto.Event;
import com.br.softstream.orchestrator_service.dto.History;
import com.br.softstream.orchestrator_service.enums.ETopics;

import static com.br.softstream.orchestrator_service.enums.EEventSource.*;
import static com.br.softstream.orchestrator_service.enums.ESagaStatus.*;
import static com.br.softstream.orchestrator_service.enums.ETopics.*;

import java.time.LocalDateTime;
import com.br.softstream.orchestrator_service.producer.SagaOrchestratorProducer;
import com.br.softstream.orchestrator_service.saga.SagaExecutionController;
import com.br.softstream.orchestrator_service.utils.JsonUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrchestratorService {

	private final JsonUtil jsonUtil;
	private final SagaOrchestratorProducer producer;
	private final SagaExecutionController sagaExecutionController;
	
	public void startSaga(Event event) {
		event.setSource(ORCHESTRATOR);
		event.setStatus(SUCCESS);
		var topic = getTopic(event);
		log.info("SAGA STARTED!");
		addHistory(event, "Saga started!");
		sendToProducerWithTopic(event, topic);
	}
	
	public void finishSagaSuccess(Event event) {
		event.setSource(ORCHESTRATOR);
		event.setStatus(SUCCESS);
		log.info("SAGA STARTED SUCCESSFULLY FOR EVENT: {}", event.getId());
		addHistory(event, "Saga finished successfully!");
		notifyFinishedSaga(event);
	}
	
	public void finishSagaFail(Event event) {
		event.setSource(ORCHESTRATOR);
		event.setStatus(FAIL);
		log.info("SAGA STARTED WITH ERRORS FOR EVENT: {}", event.getId());
		addHistory(event, "Saga finished with errors!");
		notifyFinishedSaga(event);
	}
	
	public void continueSaga(Event event) {
		var topic = getTopic(event);
		log.info("SAGA CONTINUING FOR EVENT: {}", event.getId());
		sendToProducerWithTopic(event, topic);
	}
	
	private ETopics getTopic(Event event) {
		return sagaExecutionController.getNextTopic(event);
	}
	
	private void addHistory(Event event, String message) {
		var history = History
				.builder()
				.source(event.getSource())
				.status(event.getStatus())
				.message(message)
				.createdAt(LocalDateTime.now())
				.build();
		event.addToHistory(history);
	}
	
	private void notifyFinishedSaga(Event event) {
		producer.sendEvent(jsonUtil.toJson(event), NOTIFY_ENDING.getTopic());
	}
	
	private void sendToProducerWithTopic(Event event, ETopics topic) {
		producer.sendEvent(jsonUtil.toJson(event), topic.getTopic());
	}
		
}
