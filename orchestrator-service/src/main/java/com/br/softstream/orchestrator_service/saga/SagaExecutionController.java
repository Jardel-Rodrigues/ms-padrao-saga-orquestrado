package com.br.softstream.orchestrator_service.saga;

import org.springframework.stereotype.Component;

import com.br.softstream.orchestrator_service.config.exception.ValidationException;
import com.br.softstream.orchestrator_service.dto.Event;
import com.br.softstream.orchestrator_service.enums.ETopics;
import static org.springframework.util.ObjectUtils.isEmpty;

import java.util.Arrays;

import static com.br.softstream.orchestrator_service.saga.SagaHandler.*;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class SagaExecutionController {
	
	private static final String SAGA_LOG_ID = "ORDER ID: %s | TRANSACTION ID: %s | EVENT ID: %s";

	public ETopics getNextTopic(Event event) {
		if(isEmpty(event.getSource()) || isEmpty(event.getStatus())) {
			throw new ValidationException("Source and status must be informed!");		
		}
		var topic = findTopicBySourceAndStatus(event);
		logCurrentSaga(event, topic);
		return topic;
	}
	
	private ETopics findTopicBySourceAndStatus(Event event) {
		return (ETopics) (Arrays.stream(SAGA_HANDLER)
				.filter(row -> isEventSourceAndStatusValid(event, row))
				.map(i -> i [TOPIC_INDEX])
				.findFirst()
				.orElseThrow(() -> new ValidationException("Topic not found!")));
	}
	
	private boolean isEventSourceAndStatusValid(Event event, Object[] row) {
		var source = row[EVENT_SOURCE_INDEX];
		var status = row[SAGA_STATUS_INDEX];
		return event.getSource().equals(source) && event.getStatus().equals(status);
	}
	
	private void logCurrentSaga(Event event, ETopics topic) {
		var sagaId = createSagaId(event);
		var source = event.getSource();
		switch (event.getStatus()) {
			case SUCCESS -> log.info("### CURRENT SAGA: {} | SUCCESS: {} | NEXT TOPIC: {}", source, topic, sagaId);
			case ROLLBACK_PENDING -> log.info("### CURRENT SAGA: {} | SENDING TO ROLLBACK CURRENT SERVICE: {} | NEXT TOPIC: {}", source, topic, sagaId);
			case FAIL -> log.info("### CURRENT SAGA: {} | SENDING TO ROLLBACK PREVIOUS SERVICE: {} | NEXT TOPIC: {}", source, topic, sagaId);
		}
	}
	
	private String createSagaId(Event event) {
		return String.format(SAGA_LOG_ID, event.getPayload().getId(), event.getTransactionId(), event.getId());
	}

}
