package com.eatfluencer.eatfluencer.exception;

import com.eatfluencer.eatfluencer.user.dto.ErrorCode;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
	
	private static final long serialVersionUID = 8581395900179668915L;
	
	private ErrorCode errorCode;
	
	public BusinessException(String message, ErrorCode errorCode) {
		super(message);
		this.errorCode = errorCode;
	}
	
}
