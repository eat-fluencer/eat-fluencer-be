package com.eatfluencer.eatfluencer.exception;

import lombok.Getter;

@Getter
public class PublicKeyNotFoundException extends RuntimeException {
	
	private static final long serialVersionUID = 7007090834568352758L;
	
	private ErrorCode errorCode;
	
	public PublicKeyNotFoundException(String message, ErrorCode errorCode) {
		super(message);
		this.errorCode = errorCode;
	}
	
}
