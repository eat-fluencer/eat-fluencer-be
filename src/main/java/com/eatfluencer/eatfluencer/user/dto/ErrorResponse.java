package com.eatfluencer.eatfluencer.user.dto;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class ErrorResponse {

	private boolean success = false;
	private int status;
	private String code;
	private String message;
	
	public ErrorResponse(HttpStatus httpStatus) {
		this.status = httpStatus.value();
		this.message = httpStatus.getReasonPhrase();
	}
	
	public ErrorResponse(ErrorCode errorCode) {
		this.status = errorCode.getStatus();
		this.code = errorCode.getCode();
		this.message = errorCode.getMessage();
	}
	
}
