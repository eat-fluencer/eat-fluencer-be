package com.eatfluencer.eatfluencer.exception;

import com.eatfluencer.eatfluencer.user.dto.ErrorCode;

import lombok.Getter;

@Getter
public class UserNotFoundException extends BusinessException {

	private static final long serialVersionUID = -8072188348783900634L;

	public UserNotFoundException(String message, ErrorCode errorCode) {
		super(message, errorCode);
	}
	
}
