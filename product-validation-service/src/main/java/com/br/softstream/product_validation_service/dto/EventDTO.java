package com.br.softstream.product_validation_service.dto;

import static org.springframework.util.ObjectUtils.isEmpty;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import com.br.softstream.product_validation_service.enums.ESagaStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventDTO {

	private String id;
	private String transactionId;
	private String orderId;
	private OrderDTO payload;
	private String source;
	private ESagaStatus status;
	private List<HistoryDTO> eventHistory;
	private LocalDateTime createdAt;
	
	public void addToHistory(HistoryDTO history) {
		if(isEmpty(this.eventHistory)) {
			this.eventHistory = new ArrayList<>();
		}
		this.eventHistory.add(history);
	}

}
