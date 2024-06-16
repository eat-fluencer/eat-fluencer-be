package com.eatfluencer.eatfluencer_api.controller;

import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.eatfluencer.eatfluencer_api.auth.oauth2idtoken.OAuth2IdClass;
import com.eatfluencer.eatfluencer_api.common.SuccessResponse;
import com.eatfluencer.eatfluencer_api.dto.UserDto;
import com.eatfluencer.eatfluencer_api.entity.User;
import com.eatfluencer.eatfluencer_api.service.UserService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@RequestMapping(path = "/users")
@RestController
@Slf4j
public class UserController {

    private final UserService userService;
	private final RestTemplate restTemplate;

	@Value("${kakao.restapi.key}")
    private String KAKAO_RESTAPI_KEY;

    @Value("${kakao.redirect.uri}")
    private String KAKAO_REDIRECT_URI;
	

	@GetMapping("/login-request-url")
    public ResponseEntity<SuccessResponse> getLoginLink(@RequestParam(name = "nonce", required = false) String nonce) {

		String baseUrl = "https://kauth.kakao.com/oauth/authorize";
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .queryParam("client_id", KAKAO_RESTAPI_KEY)
                .queryParam("redirect_uri", KAKAO_REDIRECT_URI)
                .queryParam("response_type", "code") // "response_type"은 "code"로 고정
                .queryParam("nonce", nonce);

   		String loginRequestUrl = builder.toString();
   	
    	SuccessResponse response = SuccessResponse.builder()
       												  .httpStatus(HttpStatus.OK)
       												  .data(Map.of("login_request_url", loginRequestUrl))
       												  .build();

       return ResponseEntity.ok()
       					 .body(response);
       
   }

   // OAuth2.0 토큰 받기
   @GetMapping("/token")
   public ResponseEntity<SuccessResponse> getIdToken(@RequestParam(name = "code", required = true) String code) throws Exception {
   	
		// ACCESS, ID, REFRESH 토큰 받기
		String tokenRequestUrl = "https://kauth.kakao.com/oauth/token";
        
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
        
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code"); // authorization_code로 고정
        params.add("client_id", KAKAO_RESTAPI_KEY);
        params.add("redirect_uri", KAKAO_REDIRECT_URI);
        params.add("code", code);
        
        // 토큰 받기
        ResponseEntity<String> tokenResponse = restTemplate.postForEntity(
        		tokenRequestUrl
        		, new HttpEntity<>(params, headers)
        		, String.class);
        
        JSONObject responesBody = new JSONObject(tokenResponse.getBody());
        
        // 응답 본문에서 id_token만 추출
        String idToken = responesBody.getString("id_token");
		
		SuccessResponse response = SuccessResponse.builder()
			.httpStatus(HttpStatus.OK)
			.data(Map.of("kakao_id_token", idToken))
			.build();
					
		return ResponseEntity.ok()
							.body(response);
       
   }
    
    // 서비스 회원가입 여부 확인
    @PostMapping("/registration-status")
    public ResponseEntity<SuccessResponse> checkUserRegistrationStatus(
    		@RequestBody Map<String, String> map) {
    	
	  	// 회원가입 여부 확인
	  	Boolean isUserSignedUp = userService.checkUserSignedUp(map);
	  	
	  	SuccessResponse response = SuccessResponse.builder()
	  											  .httpStatus(HttpStatus.OK)
	  											  .data(Map.of("signedUp", isUserSignedUp))
	  											  .build();
	  	
	  	return ResponseEntity.ok()
	    					 	 .body(response);
    	
    }
    
    // 닉네임 중복 체크
    @GetMapping("/nickname-duplication")
    public ResponseEntity<SuccessResponse> checkNickname(
    		@RequestParam(name = "nickname", required = true) String nickname) {
    	
    	Boolean isNicknameDuplicated = userService.isNicknameDuplicated(nickname);
    	
    	SuccessResponse response = SuccessResponse.builder()
				  .httpStatus(HttpStatus.OK)
				  .data(Map.of("isNicknameDuplicated", isNicknameDuplicated))
				  .build();
    	
    	return ResponseEntity.ok()
    						 .body(response);
    	
    }
    
    
    
    // 서비스 회원 정보 불러오기
    @GetMapping("/user-info")
    public ResponseEntity<SuccessResponse> getUserInfo(
    		Authentication authentication
    		) {
    	
	  	UserDto userInfo = userService.getUserInfo(authentication.getName());
	  	
	  	SuccessResponse response = SuccessResponse.builder()
	  											  .httpStatus(HttpStatus.OK)
	  											  .data(Map.of("user_info", userInfo))
	  											  .build();
	  	
	  	return ResponseEntity.ok()
	    					 	 .body(response);
    	
    }
    
    // USER 전체목록
    @GetMapping("")
    public ResponseEntity<SuccessResponse> getUserList() throws Exception {
    	
		List<UserDto> users = userService.findAllUsers()
									  .stream()
									  .map(user -> user.toDto())
									  .toList();
		
		SuccessResponse response = SuccessResponse.builder()
												  .httpStatus(HttpStatus.OK)
												  .data(Map.of("users", users))
												  .build();
        
        return ResponseEntity.ok()
        					 .body(response);
        
    }
    
    // 회원가입 처리
    @PostMapping("")
    public ResponseEntity<SuccessResponse> signUpUser(
    	    @RequestBody UserDto userDto) throws Exception {
    	
		// 회원가입
    	User signUpUser = userService.signUpUser(userDto);
    	
    	// 토큰 발급
    	Map<String, Object> tokens = userService.loginUser(userDto.getOAuth2IdClass());
    	
    	SuccessResponse response = SuccessResponse.builder()
														.httpStatus(HttpStatus.CREATED)
														.data(Map.of("user", signUpUser.toDto()
																   , "tokens", tokens))
														.build();
    	
    	return ResponseEntity.status(HttpStatus.CREATED)
    						 .body(response);
    	
    }
    
    // 로그인 (EatFluencer 토큰 발급)
    @GetMapping("/token")
    public ResponseEntity<SuccessResponse> loginUser(
    		Authentication authentication
    	  ) {
    	
    	Map<String, Object> tokens = userService.loginUser((OAuth2IdClass) authentication.getPrincipal());
    	
		SuccessResponse response = SuccessResponse.builder()
														  .httpStatus(HttpStatus.CREATED)
														  .data(tokens)
														  .build();
		
		return ResponseEntity.status(HttpStatus.CREATED)
							 .body(response);
    	
    }
    
    // 리프레시 토큰으로 엑세스, 리프레시 토큰 재발급
    @PostMapping("/refresh-token")
    public ResponseEntity<SuccessResponse> refreshToken(
    		@RequestHeader(name = "Refresh-Token", required = true) String refreshToken
    	  ) throws AuthenticationException, EntityNotFoundException {
    	
    	Map<String, Object> tokens = userService.refresh(refreshToken);
    	
		SuccessResponse response = SuccessResponse.builder()
														  .httpStatus(HttpStatus.CREATED)
														  .data(tokens)
														  .build();
		
		return ResponseEntity.status(HttpStatus.CREATED)
							 .body(response);
    	
    }
    
    // 로그아웃
    @DeleteMapping("/logout")
    public ResponseEntity<SuccessResponse> logoutUser(
    		Authentication authentication
		  ) throws Exception {

    	// 서비스 토큰 무효화
    	String userId = userService.logoutUser(authentication.getName());
    	
    	SuccessResponse response = SuccessResponse.builder()
														.httpStatus(HttpStatus.OK)
														.data(Map.of("userId", userId))
														.build();
		
    	return ResponseEntity.ok()
							 .body(response);
		
    }
    
    // 회원 탈퇴
    @DeleteMapping("")
    public ResponseEntity<SuccessResponse> cancelUser(
    		Authentication authentication
    	  ) throws Exception {
    	
    	// 회원 삭제
    	User deleteUser = userService.cancelUser(authentication.getName());
    	
    	SuccessResponse response = SuccessResponse.builder()
														.httpStatus(HttpStatus.OK)
														.data(Map.of("user", deleteUser))
														.build();
    	
		return ResponseEntity.ok()
							 .body(response);
		
    }
    
}

