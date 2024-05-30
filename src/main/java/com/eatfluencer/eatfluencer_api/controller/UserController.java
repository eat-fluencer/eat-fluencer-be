package com.eatfluencer.eatfluencer_api.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

