package com.br.softstream.orderservice.config.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionGlobalHandler {
	
	@ExceptionHandler(ValidationException.class)
	public ResponseEntity<?> handleValidationException(ValidationException ex) {
		var datalis = new ExceptionDatails(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
		return new ResponseEntity<>(datalis, HttpStatus.BAD_REQUEST);
	}
}
