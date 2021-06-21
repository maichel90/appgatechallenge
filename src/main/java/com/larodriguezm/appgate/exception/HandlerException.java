package com.larodriguezm.appgate.exception;


import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.larodriguezm.appgate.dto.ApiErrorResponseDTO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class HandlerException extends ResponseEntityExceptionHandler {
	
	@ExceptionHandler(value = { IPAddressException.class })
	public ResponseEntity<Object> handleResponseErrorException(IPAddressException ex, WebRequest request) {

		ApiErrorResponseDTO apiErrorResponseDto = new ApiErrorResponseDTO(ex.getMessage(), ex.getCode().value());

		log.error(ex.getMessage());

		return new ResponseEntity<>(apiErrorResponseDto, new HttpHeaders(), HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(value = { FileStorageException.class })
	public ResponseEntity<Object> handleResponseErrorException(FileStorageException ex, WebRequest request) {

		ApiErrorResponseDTO apiErrorResponseDto = new ApiErrorResponseDTO(ex.getMessage(), ex.getCode().value());

		log.error(ex.getMessage());

		return new ResponseEntity<>(apiErrorResponseDto, new HttpHeaders(), HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(value = { Exception.class })
	public ResponseEntity<?> handleAnyException(Exception ex, WebRequest request) {

		log.error("{}", ex.getCause());
		
		return new ResponseEntity<>(null, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);

	}

}
