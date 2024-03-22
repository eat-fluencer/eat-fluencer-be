package com.eatfluencer.eatfluencer.user;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.eatfluencer.eatfluencer.user.dto.KakaoSignUpRequestDto;
import com.eatfluencer.eatfluencer.user.dto.SuccessResponse;

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
    public ResponseEntity<SuccessResponse<String>> getKakaoLoginLink(@RequestParam(name = "nonce") String nonce) {
    	
        String baseUrl = "https://kauth.kakao.com/oauth/authorize";
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .queryParam("client_id", kakaoRestApiKey)
                .queryParam("redirect_uri", kakaoRedirectUri)
                .queryParam("response_type", "code") // "response_type"은 "code"로 고정
                .queryParam("nonce", nonce);

        String loginRequestUrl = builder.toUriString();
        
        SuccessResponse<String> response = SuccessResponse.<String>builder()
        												  .httpStatus(HttpStatus.OK)
        												  .data(Map.<String, String>of("login_request_url", loginRequestUrl))
        												  .build();

        return ResponseEntity.ok()
        					 .body(response);
        
    }
    
    // 카카오 토큰 받기
    @GetMapping("/token")
    public ResponseEntity<SuccessResponse<TokenResponseDto>> getKakaoToken(@RequestParam(name = "code") String code) throws Exception {
    	
    	TokenResponseDto tokenResponse = null;
    	
    	// 토큰 받기
		tokenResponse = kakaoUserService.requestToken(code);
        
        // idToken 유효성 검증
		kakaoUserService.validateIdToken(tokenResponse.getIdToken());
		
		SuccessResponse<TokenResponseDto> response = SuccessResponse.<TokenResponseDto>builder()
																	.httpStatus(HttpStatus.OK)
																	.data(Map.<String, TokenResponseDto>of("tokens", tokenResponse))
																	.build();
				
        return ResponseEntity.ok()
        					 .body(response);
        
    }
    
    // 회원가입 여부 확인
    @GetMapping("/users/status")
    public ResponseEntity<SuccessResponse<User>> getKakaoUserStatus(@RequestHeader(value = "Authorization") String requestHeader) throws Exception {
    	
    	// idToken 추출 후 유효성 검증
    	String idToken = kakaoUserService.extractTokenFromHttpHeader(requestHeader);
		kakaoUserService.validateIdToken(idToken);
		
		// 회원가입 여부 확인
		User checkUser = kakaoUserService.checkUserSignedUp(idToken);
		
		SuccessResponse<User> response = SuccessResponse.<User>builder()
																	.httpStatus(HttpStatus.OK)
																	.data(Map.<String, User>of("user", checkUser))
																	.build();
        
        return ResponseEntity.ok()
        					 .body(response);
        
    }	
    
    // 회원가입 처리
    @PostMapping("/users")
    public ResponseEntity<SuccessResponse<User>> kakaoSignUpUser(@RequestHeader(value = "Authorization") String requestHeader
    										  , @RequestBody KakaoSignUpRequestDto request) throws Exception {
    	
    	// idToken 추출 후 유효성 검증
    	String idToken = kakaoUserService.extractTokenFromHttpHeader(requestHeader);
		kakaoUserService.validateIdToken(idToken);
    	
		// 회원가입
    	User signUpUser = kakaoUserService.kakaoAddUser(request);
    	
    	SuccessResponse<User> response = SuccessResponse.<User>builder()
														.httpStatus(HttpStatus.OK)
														.data(Map.<String, User>of("user", signUpUser))
														.build();
    	
    	return ResponseEntity.status(HttpStatus.CREATED)
    						 .body(response);
    	
    }
    
    @PostMapping("/logout")
    public ResponseEntity<SuccessResponse<String>> kakaoLogoutUser(@RequestHeader(value = "Authorization") String requestHeader) throws Exception {
    	
    	// accessToken 추출
    	String accessToken = kakaoUserService.extractTokenFromHttpHeader(requestHeader);
		
    	// 로그아웃
    	String kakaoId = kakaoUserService.kakaoLogoutUser(accessToken);
    	
    	SuccessResponse<String> response = SuccessResponse.<String>builder()
														.httpStatus(HttpStatus.OK)
														.data(Map.<String, String>of("kakao_id", kakaoId))
														.build();
    	
		return ResponseEntity.ok()
							 .body(response);
		
    }
    
    @DeleteMapping("/users")
    public ResponseEntity<SuccessResponse<User>> kakaoCancelUser(@RequestHeader(value = "Authorization") String requestHeader) throws Exception {
    	
    	// accessToken 추출
    	String accessToken = kakaoUserService.extractTokenFromHttpHeader(requestHeader);
		
    	// 회원탈퇴
    	User deleteUser = kakaoUserService.kakaoCancelUser(accessToken);
    	
    	SuccessResponse<User> response = SuccessResponse.<User>builder()
														.httpStatus(HttpStatus.OK)
														.data(Map.<String, User>of("user", deleteUser))
														.build();
    	
		return ResponseEntity.ok()
							 .body(response);
		
    }
    
}

