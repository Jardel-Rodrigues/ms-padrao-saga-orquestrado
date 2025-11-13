package com.br.softstream.payment_service.utils;

import org.springframework.stereotype.Component;

import com.br.softstream.payment_service.dto.Event;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class JsonUtil {
	
	private final ObjectMapper objectMapper;
	
	public String toJson(Object object) {
		try {
			return objectMapper.writeValueAsString(object);
		} catch (Exception e) {
			return e.getMessage();
		}
	}


	public Event toEvent(String json) {
		try {
			return objectMapper.readValue(json, Event.class);
		} catch (Exception e) {
			return null;
		}
	}

}
