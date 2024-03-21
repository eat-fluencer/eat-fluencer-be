package com.eatfluencer.eatfluencer.user;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.eatfluencer.eatfluencer.user.dto.KakaoSignUpRequestDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@RequestMapping(path = "/api/kakao")
@RestController
@Slf4j
public class KakaoLoginController {

    @Value("${kakao.restapi.key}")
    private String kakaoRestApiKey;

    @Value("${kakao.redirect.uri}")
    private String kakaoRedirectUri;
    
    private final KakaoUserService kakaoUserService;

    // 카카오 로그인 요청 URL 반환 API
    @GetMapping("/login-request-url")
    public ResponseEntity<String> getKakaoLoginLink(@RequestParam(name = "nonce") String nonce) {
    	
        String baseUrl = "https://kauth.kakao.com/oauth/authorize";
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .queryParam("client_id", kakaoRestApiKey)
                .queryParam("redirect_uri", kakaoRedirectUri)
                .queryParam("response_type", "code") // "response_type"은 "code"로 고정
                .queryParam("nonce", nonce);

        String redirectUrl = builder.toUriString();

        return ResponseEntity.ok()
        					 .body(redirectUrl);
        
    }
    
    // 카카오 토큰 받기
    @GetMapping("/token")
    public ResponseEntity<TokenResponseDto> getKakaoToken(@RequestParam(name = "code") String code) throws Exception {
    	
    	TokenResponseDto tokenResponse = null;
    	
    	// 토큰 받기
		tokenResponse = kakaoUserService.requestToken(code);
        
        // idToken 유효성 검증
		kakaoUserService.validateIdToken(tokenResponse.getIdToken());
        
        return ResponseEntity.ok()
        					 .body(tokenResponse);
        
    }
    
    // 회원가입 여부 확인
    @GetMapping("/users/status")
    public ResponseEntity<User> getKakaoUserStatus(@RequestHeader(value = "Authorization") String requestHeader) throws Exception {
    	
    	// idToken 추출 후 유효성 검증
    	String idToken = kakaoUserService.extractTokenFromHttpHeader(requestHeader);
		kakaoUserService.validateIdToken(idToken);
		
		// 회원가입 여부 확인
		User checkUser = kakaoUserService.checkUserSignedUp(idToken);
        
        return ResponseEntity.ok()
        					 .body(checkUser);
        
    }	
    
    // 회원가입 처리
    @PostMapping("/users")
    public ResponseEntity<User> kakaoSignUpUser(@RequestHeader(value = "Authorization") String requestHeader
    										  , @RequestBody KakaoSignUpRequestDto request) throws Exception {
    	
    	// idToken 추출 후 유효성 검증
    	String idToken = kakaoUserService.extractTokenFromHttpHeader(requestHeader);
		kakaoUserService.validateIdToken(idToken);
    	
		// 회원가입 처리
    	User signUpUser = kakaoUserService.kakaoAddUser(request);
    	
    	return ResponseEntity.ok()
    						 .body(signUpUser);
    	
    }
    
    @PostMapping("/logout")
    public ResponseEntity<String> kakaoLogoutUser(@RequestHeader(value = "Authorization") String requestHeader) throws Exception {
    	
    	// accessToken 추출
    	String accessToken = kakaoUserService.extractTokenFromHttpHeader(requestHeader);
		
    	// 카카오 로그아웃
    	String kakaoId = kakaoUserService.kakaoLogoutUser(accessToken);
    	
		return ResponseEntity.ok()
							 .body(kakaoId);
		
    }
    
}

