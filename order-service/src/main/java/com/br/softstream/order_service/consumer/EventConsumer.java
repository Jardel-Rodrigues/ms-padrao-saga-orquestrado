package com.br.softstream.order_service.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.br.softstream.order_service.utils.JsonUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventConsumer {

	private final JsonUtil jsonUtil;
	
	@KafkaListener(
			groupId = "${spring.kafka.consumer.group-id}",
			topics = "${spring.kafka.topic.notify-ending}")
	public void consumerNotifyEndingEvent(String payload) {
		log.info("Recebendo evento {} para o topico notify-ending", payload);
		var event = jsonUtil.toEvent(payload);
		log.info(event.toString());
	}
}
