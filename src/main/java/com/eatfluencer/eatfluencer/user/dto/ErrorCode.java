package com.eatfluencer.eatfluencer.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorCode {
	
	// USER
	USER_NOT_FOUND(404, "USER_NOT_FOUND", "존재하지 않는 사용자"),
	
	// KAKAO LOGIN API
	PUBLIC_KEY_NOT_FOUND(404, "PUBLIC_KEY_NOT_FOUND", "존재하지 않는 Public Key"),
	TOKEN_NOT_FOUND(404, "TOKEN_NOT_FOUND", "토큰을 찾을 수 없음");
	
    private int status;
    private String code;
    private String message;
    
}
