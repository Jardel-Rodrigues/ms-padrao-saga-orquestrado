package com.br.softstream.product_validation_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderProductsDTO {
	
	private ProductDTO product;
	private int quantity;

}
