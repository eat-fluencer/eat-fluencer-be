package com.eatfluencer.eatfluencer.auth;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.eatfluencer.eatfluencer.provider.KakaoUserInfo;
import com.eatfluencer.eatfluencer.provider.OAuth2UserInfo;
import com.eatfluencer.eatfluencer.user.User;
import com.eatfluencer.eatfluencer.user.UserService;
import com.eatfluencer.eatfluencer.user.dto.SuccessResponse;
import com.eatfluencer.eatfluencer.user.dto.TokenResponseDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@RequestMapping(path = "/api/{provider}")
@RestController
@Slf4j
public class OAuth2Controller {

	private final OAuth2ServiceFactory oauth2ServiceFactory;
    private final UserService userService;

    // 로그인 요청 URL 반환
    @GetMapping("/login-request-url")
    public ResponseEntity<SuccessResponse<String>> getLoginLink(@PathVariable(name = "provider") String provider
    														  , @RequestParam(name = "nonce") String nonce) {

    	OAuth2Service oauth2Service = oauth2ServiceFactory.getService(provider);
    	
    	String loginRequestUrl = oauth2Service.getLoginLink(nonce);
    	
        SuccessResponse<String> response = SuccessResponse.<String>builder()
        												  .httpStatus(HttpStatus.OK)
        												  .data(Map.<String, String>of("login_request_url", loginRequestUrl))
        												  .build();

        return ResponseEntity.ok()
        					 .body(response);
        
    }
    
    // 토큰 받기
    @GetMapping("/token")
    public ResponseEntity<SuccessResponse<TokenResponseDto>> getKakaoToken(@PathVariable(name = "provider") String provider
			  															 , @RequestParam(name = "code") String code) throws Exception {
    	
    	OAuth2Service oauth2Service = oauth2ServiceFactory.getService(provider);
    	
    	// ACCESS, ID 토큰 받기
    	TokenResponseDto tokenResponse = oauth2Service.requestToken(code);
        
        // idToken 유효성 검증
		oauth2Service.validateIdToken(tokenResponse.getIdToken());
		
		SuccessResponse<TokenResponseDto> response = SuccessResponse.<TokenResponseDto>builder()
																	.httpStatus(HttpStatus.OK)
																	.data(Map.<String, TokenResponseDto>of("tokens", tokenResponse))
																	.build();
				
        return ResponseEntity.ok()
        					 .body(response);
        
    }
    
    // 사용자 정보 요청
    @GetMapping("/users/info")
    public ResponseEntity<SuccessResponse<OAuth2UserInfo>> getUserInfo(@RequestHeader(value = "Authorization") String requestHeader
																     , @PathVariable(name = "provider") String provider) {
    	
    	OAuth2Service oauth2Service = oauth2ServiceFactory.getService(provider);
    	
    	// accessToken 추출
    	String accessToken = userService.extractTokenFromHttpHeader(requestHeader);
		
		// OAuth2 API 공급자에게 사용자 정보 요청
		OAuth2UserInfo userInfo = oauth2Service.requestUserInfo(accessToken);
		
		SuccessResponse<OAuth2UserInfo> response = SuccessResponse.<OAuth2UserInfo>builder()
																	.httpStatus(HttpStatus.OK)
																	.data(Map.<String, OAuth2UserInfo>of("user_info", userInfo))
																	.build();
        
        return ResponseEntity.ok()
        					 .body(response);
    	
    }
    
    // 회원가입 여부 확인
    @GetMapping("/users/{providerId}/status")
    public ResponseEntity<SuccessResponse<Boolean>> getUserStatus(@RequestHeader(value = "Authorization") String requestHeader
															    , @PathVariable(name = "providerId") String providerId
															    , @PathVariable(name = "provider") String provider) throws Exception {
    	
    	OAuth2Service oauth2Service = oauth2ServiceFactory.getService(provider);
    	
    	// idToken 추출 후 유효성 검증
    	String idToken = userService.extractTokenFromHttpHeader(requestHeader);
		oauth2Service.validateIdToken(idToken);
		
		// 회원가입 여부 확인
		Boolean isUserSignedUp = userService.checkUserSignedUp(providerId, provider);
		
		SuccessResponse<Boolean> response = SuccessResponse.<Boolean>builder()
																	.httpStatus(HttpStatus.OK)
																	.data(Map.<String, Boolean>of("signedUp", isUserSignedUp))
																	.build();
        
        return ResponseEntity.ok()
        					 .body(response);
        
    }	
    
    // 회원가입 처리
    @PostMapping("/users")
    public ResponseEntity<SuccessResponse<User>> signUpUser(@RequestHeader(value = "Authorization") String requestHeader
    														   , @PathVariable(name = "provider") String provider
    										   				   , @RequestBody KakaoUserInfo request) throws Exception {
    	
    	OAuth2Service oauth2Service = oauth2ServiceFactory.getService(provider);
    	
    	// idToken 추출 후 유효성 검증
    	String idToken = userService.extractTokenFromHttpHeader(requestHeader);
		oauth2Service.validateIdToken(idToken);
    	
		// 회원가입
    	User signUpUser = userService.signUpUser(request);
    	
    	SuccessResponse<User> response = SuccessResponse.<User>builder()
														.httpStatus(HttpStatus.OK)
														.data(Map.<String, User>of("user", signUpUser))
														.build();
    	
    	return ResponseEntity.status(HttpStatus.CREATED)
    						 .body(response);
    	
    }
    
    @PostMapping("/users/logout")
    public ResponseEntity<SuccessResponse<String>> logoutUser(@RequestHeader(value = "Authorization") String requestHeader
    															 , @PathVariable(name = "provider") String provider) throws Exception {
    	
    	OAuth2Service oauth2Service = oauth2ServiceFactory.getService(provider);
    	
    	// accessToken 추출
    	String accessToken = userService.extractTokenFromHttpHeader(requestHeader);
		
    	// 로그아웃
    	String providerId = oauth2Service.logoutUser(accessToken);
    	
    	SuccessResponse<String> response = SuccessResponse.<String>builder()
														.httpStatus(HttpStatus.OK)
														.data(Map.<String, String>of("providerId", providerId))
														.build();
    	
		return ResponseEntity.ok()
							 .body(response);
		
    }
    
    @DeleteMapping("/users")
    public ResponseEntity<SuccessResponse<User>> cancelUser(@RequestHeader(value = "Authorization") String requestHeader
			 												   , @PathVariable(name = "provider") String provider) throws Exception {
    	
    	OAuth2Service oauth2Service = oauth2ServiceFactory.getService(provider);
    	
    	// accessToken 추출
    	String accessToken = userService.extractTokenFromHttpHeader(requestHeader);

    	// API 제공자 연결끊기
    	String providerId = oauth2Service.unlink(accessToken);
    	
    	// 회원탈퇴
    	User deleteUser = userService.cancelUser(providerId, provider);
    	
    	SuccessResponse<User> response = SuccessResponse.<User>builder()
														.httpStatus(HttpStatus.OK)
														.data(Map.<String, User>of("user", deleteUser))
														.build();
    	
		return ResponseEntity.ok()
							 .body(response);
		
    }
    
}

