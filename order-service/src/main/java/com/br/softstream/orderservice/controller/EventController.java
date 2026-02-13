package com.br.softstream.orderservice.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.br.softstream.orderservice.document.Event;
import com.br.softstream.orderservice.dto.EventFiltersDTO;
import com.br.softstream.orderservice.service.EventService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/event")
public class EventController {
	
	private final EventService eventService;
	
	@GetMapping
	public Event findByFilters(EventFiltersDTO filters) {
		return eventService.findByFilters(filters);
	}
	
	@GetMapping(value = "/all")
	public List<Event> findAll() {
		return eventService.findAll();
	}
}
