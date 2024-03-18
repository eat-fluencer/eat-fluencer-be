package com.eatfluencer.eatfluencer.user;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.eatfluencer.eatfluencer.user.dto.KakaoSignUpRequestDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
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
    public ResponseEntity<String> getKakaoToken(@RequestParam(name = "code") String code) {
    	
    	JSONObject tokenResponse = null;
    	
    	// 토큰 받기
        try {
			tokenResponse = kakaoUserService.requestToken(code);
			log.info("====================" + tokenResponse.toString());
			kakaoUserService.checkUserSignedUp(tokenResponse.getString("id_token"));
        } catch (NoUserException e) { // 회원가입 필요
        	return ResponseEntity.ok()
        						 .body(tokenResponse.put("signed_up", false).toString());
        } catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
								 .build();
		}
        
        return ResponseEntity.ok()
        					 .body(tokenResponse.toString());
        
    }
    
    // 카카오 회원가입 처리
    @PostMapping("/users")
    public ResponseEntity<User> kakaoSignUpUser(@RequestBody KakaoSignUpRequestDto request) {
    	
    	User signUpUser = null;
    	
    	// 회원가입 처리
        try {
        	signUpUser = kakaoUserService.kakaoAddUser(request);
        } catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
								 .build();
		}
    	
    	return ResponseEntity.ok()
    						 .body(signUpUser);
    	
    }
    
    
}

