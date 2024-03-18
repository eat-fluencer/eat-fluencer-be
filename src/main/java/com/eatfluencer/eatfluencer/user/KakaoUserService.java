package com.eatfluencer.eatfluencer.user;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.impl.JWTParser;
import com.eatfluencer.eatfluencer.common.PublicKeyNotFoundException;
import com.eatfluencer.eatfluencer.tag.Tag;
import com.eatfluencer.eatfluencer.user.dto.KakaoSignUpRequestDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
@Slf4j
public class KakaoUserService {

	private final UserRepository userRepository;
	private final UserTagRepository userTagRepository;
	private final RestTemplate restTemplate;
	private final JWTParser jwtParser;
	
    @Value("${kakao.restapi.key}")
    private String kakaoRestApiKey;

    @Value("${kakao.redirect.uri}")
    private String kakaoRedirectUri;
	
	// 카카오 공개 키 kid로 검색 후 RSAPublicKey로 만들어 반환
	private RSAPublicKey getPublicKeyByKid(String idToken) throws Exception {
		
		// kid 구하기
		String kid = jwtParser.parseHeader(idToken).getKeyId();
		
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
		throw new PublicKeyNotFoundException("Public key not found for the kid: " + kid);
        
	}
	
	// id_token 검증
	private void validateIdToken(String idToken) throws Exception {
		
	    Algorithm algorithm = Algorithm.RSA256(getPublicKeyByKid(idToken));
	    JWTVerifier verifier = JWT.require(algorithm)
	        .withIssuer("https://kauth.kakao.com") // iss가 카카오인지
	        .withAudience(kakaoRestApiKey) // aud가 서비스 앱 키와 일치하는지
	        .build(); // exp는 default로 확인
	    verifier.verify(idToken);
		
	}
	
	// 가입 여부 확인
	public void checkUserSignedUp(String idToken) throws NoUserException {
		String subject = new JSONObject(idToken).getString("sub");
		userRepository.findBySubject(subject)
			.orElseThrow(() -> new NoUserException("user does not exist with subject: " + subject));
	}
	
	// 카카오 토큰 요청
    public JSONObject requestToken(String code) throws Exception {
    	
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
        
        // id_token 유효성 검증
        validateIdToken(idToken);
        
        // 추출한 값만 반환
        JSONObject tokenResponse = new JSONObject();
        tokenResponse.append("access_token", accessToken);
        tokenResponse.append("id_token", idToken);
        tokenResponse.append("expires_in", expiresIn);
        
        return tokenResponse;
        
    }
	
	// 가입 처리
	public User kakaoAddUser(KakaoSignUpRequestDto request) {
		User addUser = userRepository.save(request.toEntity());
		List<Tag> tags = request.getTags();
		tags.stream()
			.forEach(tag -> userTagRepository.save(new UserTag(addUser, tag)));
		return addUser;
	}

}
