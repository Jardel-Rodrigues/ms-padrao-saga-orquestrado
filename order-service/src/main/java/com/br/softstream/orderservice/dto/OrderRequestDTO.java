package com.br.softstream.orderservice.dto;

import java.util.List;

import com.br.softstream.orderservice.document.OrderProducts;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequestDTO {

	private List<OrderProducts> products;
	
}
