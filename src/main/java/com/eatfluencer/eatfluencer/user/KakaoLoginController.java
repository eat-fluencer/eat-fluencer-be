package com.eatfluencer.eatfluencer.user;

import java.util.Map;
import java.util.Random;

import javax.swing.text.html.HTML;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping(path = "/api/kakao")
@RestController
public class KakaoLoginController {

    @Value("${kakao.restapi.key}")
    private String kakaoRestApiKey;

    @Value("${kakao.redirect.uri}")
    private String kakaoRedirectUri;

    private final RestTemplate restTemplate;

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
    
    // 카카오 토큰 요청 & 발급
    @GetMapping("/token")
    public ResponseEntity<String> getKakaoToken(@RequestParam(name = "code") String code) {
    	
        String tokenRequestUrl = "https://kauth.kakao.com/oauth/token";
        
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
        
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code"); // authorization_code로 고정
        params.add("client_id", kakaoRestApiKey);
        params.add("redirect_uri", kakaoRedirectUri);
        params.add("code", code);
        
        ResponseEntity<String> tokenResponse = restTemplate.postForEntity(
        		tokenRequestUrl
        		, new HttpEntity<>(params, headers)
        		, String.class);
        
        JSONObject tokenResponseBody = new JSONObject(tokenResponse.getBody());
        
        // 가입 및 로그인 처리
        
        
        return null;
        
        
    }
}

