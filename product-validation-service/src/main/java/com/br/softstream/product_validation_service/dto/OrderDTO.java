package com.br.softstream.product_validation_service.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {

	private String id;
	private List<OrderProductsDTO> products;
	private LocalDateTime createdAt;
	private String transactionId;
	private double totalAmount;
	private int totalItems;
	
}
