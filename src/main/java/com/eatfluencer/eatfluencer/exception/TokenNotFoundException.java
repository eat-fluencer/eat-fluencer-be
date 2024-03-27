package com.eatfluencer.eatfluencer.exception;

import com.eatfluencer.eatfluencer.user.dto.ErrorCode;

import lombok.Getter;

@Getter
public class TokenNotFoundException extends BusinessException {

	private static final long serialVersionUID = -1171651983391838458L;

	public TokenNotFoundException(String message, ErrorCode errorCode) {
		super(message, errorCode);
	}
	
}
