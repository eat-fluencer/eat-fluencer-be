package com.eatfluencer.eatfluencer.user;

import java.util.Map;

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

    // 카카오 로그인 요청 URL
    @GetMapping("/login-request-url")
    public ResponseEntity<String> getKakaoLoginLink() {
    	
        String baseUrl = "https://kauth.kakao.com/oauth/authorize";
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .queryParam("client_id", kakaoRestApiKey)
                .queryParam("redirect_uri", kakaoRedirectUri)
                .queryParam("response_type", "code");

        String redirectUrl = builder.toUriString();

        return ResponseEntity.ok()
        					 .body(redirectUrl);
        
    }
    
    // 카카오 토큰 받기
    @GetMapping("/token")
    public String kakaoLogin(@RequestParam(name = "code") String code) {
    	
        String tokenRequestUrl = "https://kauth.kakao.com/oauth/token";
        
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
        
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code"); // authorization_code로 고정
        params.add("client_id", kakaoRestApiKey);
        params.add("redirect_uri", kakaoRedirectUri);
        params.add("code", code);
        
        ResponseEntity<String> response = restTemplate.postForEntity(
        		tokenRequestUrl
        		, new HttpEntity<>(params, headers)
        		, String.class);
        
        JSONObject responseBody = new JSONObject(response.getBody());
        
        // 로그인 처리
        
        
        return null;
        
        
    }
}

