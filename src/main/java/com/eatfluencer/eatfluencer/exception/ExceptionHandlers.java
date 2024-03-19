package com.eatfluencer.eatfluencer.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class ExceptionHandlers {
	
	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException e) {
		log.error("handleUserNotFoundException", e);
		ErrorResponse response = new ErrorResponse(e.getErrorCode());
		return ResponseEntity.status(e.getErrorCode().getStatus())
							 .body(response);
	}
	
	@ExceptionHandler(PublicKeyNotFoundException.class)
	public ResponseEntity<ErrorResponse> handlePublicKeyNotFoundException(PublicKeyNotFoundException e) {
		log.error("handlePublicKeyNotFoundException", e);
		ErrorCode errorCode = e.getErrorCode();
		ErrorResponse response = new ErrorResponse(errorCode);
		return ResponseEntity.status(errorCode.getStatus())
							 .body(response);
	}
	
}
