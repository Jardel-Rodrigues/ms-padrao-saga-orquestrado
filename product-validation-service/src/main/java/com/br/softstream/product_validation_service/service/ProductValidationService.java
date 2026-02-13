package com.br.softstream.product_validation_service.service;

import static org.springframework.util.ObjectUtils.isEmpty;

import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import com.br.softstream.product_validation_service.config.exception.ValidationException;
import com.br.softstream.product_validation_service.dto.EventDTO;
import com.br.softstream.product_validation_service.dto.HistoryDTO;
import com.br.softstream.product_validation_service.dto.OrderProductsDTO;
import static com.br.softstream.product_validation_service.enums.ESagaStatus.*;
import com.br.softstream.product_validation_service.model.Validation;
import com.br.softstream.product_validation_service.producer.KafkaProducer;
import com.br.softstream.product_validation_service.repository.ProductRepository;
import com.br.softstream.product_validation_service.repository.ValidationRepository;
import com.br.softstream.product_validation_service.utils.JsonUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductValidationService {

	private static final String CURRENT_SOURCE = "PRODUCT_VALIDATION_SERVICE";
	
	private final JsonUtil jsonUtils;
	private final KafkaProducer producer;
	private final ProductRepository productRepository;
	private final ValidationRepository validationRepository;
	
	public void validationExistingProduct(EventDTO event) {
		try {
			checkCurrentValidation(event);
			createValidation(event, true);
			handleSuccess(event);
			
		} catch (Exception ex) {
			log.error("Error trying to validate products: {}", ex);
			handleFailCurrentNotExecuted(event, ex.getMessage());
		}
		producer.sendEvent(jsonUtils.toJson(event));		
	}
	
	public void rollbackEvent(EventDTO event) {
		changeValidationToFail(event);	
		event.setStatus(FAIL);
		event.setSource(CURRENT_SOURCE);
		addHistory(event, "Rollback executed on product validation!");
		producer.sendEvent(jsonUtils.toJson(event));		
	}

	private void changeValidationToFail(EventDTO event) {
		validationRepository
			.findByOrderIdAndTransactionId(event.getPayload().getId(), event.getTransactionId())
			.ifPresentOrElse(validation -> {
				validation.setSucess(false);
				validationRepository.save(validation);
			},
					()-> createValidation(event, false));		
	}

	private void handleFailCurrentNotExecuted(EventDTO event, String message) {
		event.setStatus(ROLLBACK_PENDING);
		event.setSource(CURRENT_SOURCE);
		addHistory(event, "Fail to validate products: ".concat(message));		
	}

	private void handleSuccess(EventDTO event) {
		event.setStatus(SUCCESS);
		event.setSource(CURRENT_SOURCE);
		addHistory(event, "Products are validated successfully!");
	}
	
	private void addHistory(EventDTO event, String message) {
		var history = HistoryDTO
				.builder()
				.source(event.getSource())
				.status(event.getStatus())
				.message(message)
				.createdAt(LocalDateTime.now())
				.build();
		event.addToHistory(history);
	}

	private void createValidation(EventDTO event, boolean sucess) {
		var validation = Validation
				.builder()
				.orderId(event.getPayload().getId())
				.transactionId(event.getTransactionId())
				.sucess(sucess)
				.build();
		validationRepository.save(validation);
	}

	private void checkCurrentValidation(EventDTO event) {
		validateProductsInformed(event);
		if(validationRepository.existsByOrderIdAndTransactionId(event.getOrderId(), event.getTransactionId())) {
			throw new ValidationException("There's another transactionId for this validation!");
		}
		
		event.getPayload().getProducts().forEach(product -> {
			validateProductsInformed(product);
			validateExistingProduct(product.getProduct().getCode());
		});		
	}

	private void validateProductsInformed(EventDTO event) {
		if(isEmpty(event.getPayload()) || isEmpty(event.getPayload().getProducts())) {
			throw new ValidationException("Products list is empty!");
			
		}
		if(isEmpty(event.getPayload().getId()) || isEmpty(event.getPayload().getTransactionId())) {
			throw new ValidationException("OrderID and TransactionID must be informed!");
			
		}
	}
	
	private void validateProductsInformed(OrderProductsDTO product) {
		if(isEmpty(product.getProduct()) || isEmpty(product.getProduct().getCode())) {
			throw new ValidationException("Product must by informed!");
			
		}
	}
	
	private void validateExistingProduct(String code) {
		if(!productRepository.existsByCode(code)) {
			throw new ValidationException("Product does not exist in database!");		
		}
	}
	
}
