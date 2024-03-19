package com.eatfluencer.eatfluencer.exception;

import lombok.Getter;

@Getter
public class UserNotFoundException extends Exception {

	private static final long serialVersionUID = -8072188348783900634L;

	private ErrorCode errorCode;
	
	public UserNotFoundException(String message, ErrorCode errorCode) {
		super(message);
		this.errorCode = errorCode;
	}
	
}
