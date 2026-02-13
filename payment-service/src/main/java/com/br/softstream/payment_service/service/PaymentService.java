package com.br.softstream.payment_service.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.br.softstream.payment_service.config.exception.ValidationException;
import com.br.softstream.payment_service.dto.Event;
import com.br.softstream.payment_service.dto.History;
import com.br.softstream.payment_service.enums.EPaymentStatus;
import com.br.softstream.payment_service.enums.ESagaStatus;
import com.br.softstream.payment_service.model.Payment;
import com.br.softstream.payment_service.producer.KafkaProducer;
import com.br.softstream.payment_service.repository.PaymentRepository;
import com.br.softstream.payment_service.utils.JsonUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {
	
	private static final String CURRENT_SOURCE = "PAYMENT_SERVICE";
	private static final Double REDUCE_SUM_VALUE = 0.0;
	private static final Double MIN_AMOUNT_VALUE = 0.1; 
	
	private final JsonUtil jsonUtil;
	private final KafkaProducer producer;
	private final PaymentRepository paymentRepository;
	
	public void realizePayment(Event event) {
		try {
			checkCurrentValidation(event);
			createPendingPayment(event);
			var payment = findByOrderIdAndTransactionId(event);
			validateAmount(payment.getTotalAmount());
			changePaymentToSuccess(payment);
			handleSuccess(event);
		} catch (Exception ex) {
			log.error("Error trying to make payment:", ex);
			handleFailCurrentNotExecuted(event, ex.getMessage());
		}
		producer.sendEvent(jsonUtil.toJson(event));
	}
	
	public void realizeRefund(Event event) {
		event.setStatus(ESagaStatus.FAIL);
		event.setSource(CURRENT_SOURCE);
		try
		{
			changePaymentStatusToRefund(event);
			addHistory(event, "Rollback executed for payment!");
		} catch (Exception ex) {
			addHistory(event, "Rollback not executed for payment: ".concat(ex.getMessage()));
		}		
		producer.sendEvent(jsonUtil.toJson(event));
	}
	
	private void createPendingPayment(Event event) {
		var totalAmount = calculateTotalAmount(event);
		var totalItems = calculateTotalItems (event);
		var payment = Payment
				.builder()
				.orderId(event.getPayload().getId())
				.transactionId(event.getTransactionId())
				.totalAmount(totalAmount)
				.totalItems(totalItems)
				.build();
		save(payment);
		setEventAmountItems(event, payment);
	}
	
	private double calculateTotalAmount(Event event) {
		return event
				.getPayload()
				.getProducts()
				.stream()
				.map(product -> product.getQuantity() * product.getProduct().getUnitValue())
				.reduce(REDUCE_SUM_VALUE, Double::sum);
	}
	
	private int calculateTotalItems(Event event) {
		return event
				.getPayload()
				.getProducts()
				.stream()
				.map(product -> product.getQuantity())
				.reduce(REDUCE_SUM_VALUE.intValue(), Integer::sum);
	}

	private void  checkCurrentValidation(Event event) {
		if (paymentRepository.existsByOrderIdAndTransactionId(event.getPayload().getId(), event.getTransactionId())) {
			throw new ValidationException("There's another transactionId for this validation!");
		}
	}
	
	private void validateAmount(double amount) {
		if (amount <= MIN_AMOUNT_VALUE) {
			throw new ValidationException("The minimum amount for payment is ".concat(MIN_AMOUNT_VALUE.toString()));
		}
	}
	
	private void handleFailCurrentNotExecuted(Event event, String message) {
		event.setStatus(ESagaStatus.ROLLBACK_PENDING);
		event.setSource(CURRENT_SOURCE);
		addHistory(event, "Fail to realize payment: ".concat(message));		
	}
	
	private void changePaymentStatusToRefund(Event event) {
		var payment = findByOrderIdAndTransactionId(event);
		payment.setStatus(EPaymentStatus.REFUND);
		setEventAmountItems(event, payment);
		save(payment);
	}
	
	private Payment findByOrderIdAndTransactionId(Event event) {
		return paymentRepository
				.findByOrderIdAndTransactionId(event.getPayload().getId(), event.getTransactionId())
				.orElseThrow(() -> new ValidationException("Payment not found for orderId and transactionId!"));
	}
	
	private void setEventAmountItems(Event event, Payment payment) {
		event.getPayload().setTotalAmount(payment.getTotalAmount());
		event.getPayload().setTotalItems(payment.getTotalItems());
	}
	
	private void changePaymentToSuccess(Payment payment) {
		payment.setStatus(EPaymentStatus.SUCCESS);
		save(payment);
	}
	
	private void handleSuccess(Event event) {
		event.setStatus(ESagaStatus.SUCCESS);
		event.setSource(CURRENT_SOURCE);
		addHistory(event, "Products are validated successfully!");
	}
	
	private void addHistory(Event event, String message) {
		var history = History
				.builder()
				.source(event.getSource())
				.status(event.getStatus())
				.message(message)
				.createdAt(LocalDateTime.now())
				.build();
		event.addToHistory(history);
	}
	
	private void save(Payment payment) {
		paymentRepository.save(payment);
	}

}
