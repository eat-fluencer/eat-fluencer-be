package com.eatfluencer.eatfluencer.auth;

import org.springframework.transaction.annotation.Transactional;

import com.eatfluencer.eatfluencer.exception.UserNotFoundException;
import com.eatfluencer.eatfluencer.provider.OAuth2UserInfo;
import com.eatfluencer.eatfluencer.user.dto.TokenResponseDto;

@Transactional(readOnly = true)
public interface OAuth2Service {
	
	// OAuth2 제공자 반환
	public String getProvider();
	
	// 로그인 링크 반환
	public String getLoginLink(String nonce);
	
	// id_token 검증
	public void validateIdToken(String idToken) throws Exception;
	
	// 토큰 요청
    public TokenResponseDto requestToken(String code) throws Exception;
    
    // 사용자 정보 요청
	public OAuth2UserInfo requestUserInfo(String accessToken);
	
	// 로그아웃
	public String logoutUser(String accessToken);
	
	// 연결 끊기
	@Transactional
	public String unlink(String accessToken) throws UserNotFoundException;

}
