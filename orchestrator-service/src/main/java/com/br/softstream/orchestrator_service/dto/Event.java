package com.br.softstream.orchestrator_service.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.br.softstream.orchestrator_service.enums.EEventSource;
import com.br.softstream.orchestrator_service.enums.ESagaStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Event {

	private String id;
	private String transactionId;
	private String orderId;
	private Order payload;
	private EEventSource source;
	private ESagaStatus status;
	private List<History> eventHistory;
	private LocalDateTime createdAt;

}
