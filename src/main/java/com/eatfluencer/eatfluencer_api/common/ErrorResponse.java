package com.eatfluencer.eatfluencer_api.common;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class ErrorResponse {

	private boolean success = false;
	private int status;
	private String code;
	private String message;
	
	public ErrorResponse(HttpStatus httpStatus, String message) {
		this.status = httpStatus.value();
		this.code = httpStatus.getReasonPhrase();
		this.message = message;
	}
	
//	public ErrorResponse(ErrorCode errorCode) {
//		this.status = errorCode.getStatus();
//		this.code = errorCode.getCode();
//		this.message = errorCode.getMessage();
//	}
	
}
