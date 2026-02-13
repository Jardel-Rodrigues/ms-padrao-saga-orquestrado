package com.br.softstream.orderservice.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.br.softstream.orderservice.document.Order;
import com.br.softstream.orderservice.dto.OrderRequestDTO;
import com.br.softstream.orderservice.service.OrderService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/order")
public class OrderController {

	private final OrderService service;

	@PostMapping
	public Order createOrder(@RequestBody OrderRequestDTO request) {
		return service.createOrder(request);
	}
}
