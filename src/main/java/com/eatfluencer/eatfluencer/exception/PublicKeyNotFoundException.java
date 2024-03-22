package com.eatfluencer.eatfluencer.exception;

import com.eatfluencer.eatfluencer.user.dto.ErrorCode;

import lombok.Getter;

@Getter
public class PublicKeyNotFoundException extends BusinessException {

	private static final long serialVersionUID = 7007090834568352758L;
	
	public PublicKeyNotFoundException(String message, ErrorCode errorCode) {
		super(message, errorCode);
	}
	
}
