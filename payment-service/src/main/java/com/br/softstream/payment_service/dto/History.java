package com.br.softstream.payment_service.dto;

import java.time.LocalDateTime;

import com.br.softstream.payment_service.enums.ESagaStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class History {
	
	private String source;
	private ESagaStatus status;
	private String message;
	private LocalDateTime createdAt;

}
