package com.eatfluencer.eatfluencer.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorCode {
	
	// USER
	USER_NOT_FOUND(404, "USER_NOT_FOUND", "존재하지 않는 사용자"),
	
	// KAKAO LOGIN API
	PUBLIC_KEY_NOT_FOUND(404, "PUBLIC_KEY_NOT_FOUND", "존재하지 않는 Public Key");
	
    private int status;
    private String code;
    private String message;
    
}
