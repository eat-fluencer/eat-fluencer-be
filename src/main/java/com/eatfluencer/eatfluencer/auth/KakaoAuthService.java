package com.eatfluencer.eatfluencer.auth;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.impl.JWTParser;
import com.eatfluencer.eatfluencer.exception.PublicKeyNotFoundException;
import com.eatfluencer.eatfluencer.exception.TokenNotFoundException;
import com.eatfluencer.eatfluencer.exception.UserNotFoundException;
import com.eatfluencer.eatfluencer.provider.KakaoUserInfo;
import com.eatfluencer.eatfluencer.provider.OAuth2UserInfo;
import com.eatfluencer.eatfluencer.tag.UserTagRepository;
import com.eatfluencer.eatfluencer.user.UserRepository;
import com.eatfluencer.eatfluencer.user.dto.ErrorCode;
import com.eatfluencer.eatfluencer.user.dto.SuccessResponse;
import com.eatfluencer.eatfluencer.user.dto.TokenResponseDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
@Slf4j
public class KakaoAuthService implements OAuth2Service {

	private static final String PROVIDER = "kakao";
	
	private final UserRepository userRepository;
	private final UserTagRepository userTagRepository;
	private final RestTemplate restTemplate;
	private final JWTParser jwtParser;
	
    @Value("${kakao.restapi.key}")
    private String kakaoRestApiKey;

    @Value("${kakao.redirect.uri}")
    private String kakaoRedirectUri;
	
    @Override
    public String getProvider() {
    	return PROVIDER;
    }
    
    // 카카오 로그인 요청 URL 반환
    @Override
    public String getLoginLink(String nonce) {
    	
        String baseUrl = "https://kauth.kakao.com/oauth/authorize";
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .queryParam("client_id", kakaoRestApiKey)
                .queryParam("redirect_uri", kakaoRedirectUri)
                .queryParam("response_type", "code") // "response_type"은 "code"로 고정
                .queryParam("nonce", nonce);

        return builder.toUriString();
        
    }
    
	// 카카오 공개 키 kid로 검색 후 RSAPublicKey로 만들어 반환
	private RSAPublicKey getPublicKeyByKid(String idToken) throws Exception {
		
		// idToken header 디코드해서 String으로 변환
		String header = new String(Base64.getDecoder().decode(idToken.split("\\.")[0]));
		
		// Header에서 kid 구하기
		String kid = jwtParser.parseHeader(header).getKeyId();
		
		// 공개키 목록 조회
		String publicKeyListUrl = "https://kauth.kakao.com/.well-known/jwks.json";
		String responseBodyAsString = restTemplate.getForObject(
						        		publicKeyListUrl
						        		, String.class);
		JSONObject responseBody = new JSONObject(responseBodyAsString);
        JSONArray keys = new JSONArray(responseBody.getJSONArray("keys"));
        
        for(int i = 0; i < keys.length(); i++) {
        	JSONObject key = keys.getJSONObject(i);
        	if(kid.equals(key.getString("kid"))) { // 특정 kid를 가진 key 찾으면 퍼블릭키 생성 후 반환
        		 BigInteger modulus = new BigInteger(1, Base64.getUrlDecoder().decode(key.getString("n")));
                 BigInteger exponent = new BigInteger(1, Base64.getUrlDecoder().decode(key.getString("e")));
                 RSAPublicKeySpec spec = new RSAPublicKeySpec(modulus, exponent);
                 KeyFactory factory = KeyFactory.getInstance(key.getString("kty")); // 카카오에서 "kty" : "RSA"로 고정
                 return (RSAPublicKey) factory.generatePublic(spec);
        	}
        }
        
        // 특정 kid를 가진 key가 없으면 예외처리 
		throw new PublicKeyNotFoundException("Public key not found for the kid: " + kid, ErrorCode.PUBLIC_KEY_NOT_FOUND);
        
	}
	
	// id_token 검증
    @Override
	public void validateIdToken(String idToken) throws Exception {
		
	    Algorithm algorithm = Algorithm.RSA256(getPublicKeyByKid(idToken));
	    JWTVerifier verifier = JWT.require(algorithm)
	        .withIssuer("https://kauth.kakao.com") // iss가 카카오인지
	        .withAudience(kakaoRestApiKey) // aud가 서비스 앱 키와 일치하는지
	        .build(); // exp는 default로 확인
	    verifier.verify(idToken);
		
	}
	
	// 카카오 토큰 요청
    @Override
    public TokenResponseDto requestToken(String code) throws Exception {
    	
    	String tokenRequestUrl = "https://kauth.kakao.com/oauth/token";
        
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
        
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code"); // authorization_code로 고정
        params.add("client_id", kakaoRestApiKey);
        params.add("redirect_uri", kakaoRedirectUri);
        params.add("code", code);
        
        // 토큰 받기
        ResponseEntity<String> response = restTemplate.postForEntity(
        		tokenRequestUrl
        		, new HttpEntity<>(params, headers)
        		, String.class);
        
        JSONObject responesBody = new JSONObject(response.getBody());
        
        // 응답 본문에서 access_token, id_token, expires_in만 추출
        String accessToken = responesBody.getString("access_token");
        String idToken = responesBody.getString("id_token");
        int expiresIn = responesBody.getInt("expires_in");
      
        // 추출한 값만 반환
        TokenResponseDto tokenResponse = TokenResponseDto.builder()
        												 .idToken(idToken)
        												 .accessToken(accessToken)
        												 .expiresIn(expiresIn)
        												 .build();
        
        return tokenResponse;
        
    }
	
	// 카카오 사용자 정보 요청
    @Override
	public OAuth2UserInfo requestUserInfo(String accessToken) {
		
		String requestUserInfoUrl = "https://kapi.kakao.com/v1/oidc/userinfo";
		
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(accessToken);
		
		// 사용자 정보 요청
		ResponseEntity<String> response = restTemplate.exchange(
				requestUserInfoUrl
			  , HttpMethod.GET
			  , new HttpEntity<String>(headers)
			  , String.class);
		
		return new KakaoUserInfo(new JSONObject(response.getBody()));
		
	}
	
	// 로그아웃
    @Override
	public String logoutUser(String accessToken) {
		
		String logoutUrl = "https://kapi.kakao.com/v1/user/logout";
		
		HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        
     	// 로그아웃 요청
        ResponseEntity<String> response = restTemplate.postForEntity(
        		logoutUrl
        		, new HttpEntity<>(headers)
        		, String.class);
        
        Long providerIdAsLong = new JSONObject(response.getBody()).getLong("id");
        String providerId = Long.toString(providerIdAsLong);
        
        return providerId;
		
	}
	
	// 연결 끊기
    @Override
	public String unlink(String accessToken) throws UserNotFoundException {
		
		String unlinkRequestUrl = "https://kapi.kakao.com/v1/user/unlink";
		
		HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        
     	// 연결끊기 요청
        ResponseEntity<String> response = restTemplate.postForEntity(
        		unlinkRequestUrl
        		, new HttpEntity<>(headers)
        		, String.class);
        
        Long providerIdAsLong = new JSONObject(response.getBody()).getLong("id");
        String providerId = Long.toString(providerIdAsLong);
        
        return providerId;
		
	} 

}
