package com.eatfluencer.eatfluencer.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.eatfluencer.eatfluencer.user.dto.ErrorResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class ExceptionHandlers {
	
	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e) {
		log.error("handle" + e.getClass().getSimpleName(), e);
		ErrorResponse response = new ErrorResponse(e.getErrorCode());
		return ResponseEntity.status(e.getErrorCode().getStatus())
							 .body(response);
	}
	
}
