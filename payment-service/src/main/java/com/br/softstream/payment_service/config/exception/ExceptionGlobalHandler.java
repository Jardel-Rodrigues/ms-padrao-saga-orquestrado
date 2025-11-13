package com.br.softstream.payment_service.config.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionGlobalHandler {
	
	@ExceptionHandler(ValidationException.class)
	public ResponseEntity<?> handleValidationException(ValidationException validationException) {
		var datalis = new ExceptionDatails(HttpStatus.BAD_REQUEST.value(), validationException.getMessage());
		return new ResponseEntity<>(datalis, HttpStatus.BAD_REQUEST);
	}
}
