package com.eatfluencer.eatfluencer.user;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Optional;
import java.util.StringTokenizer;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.impl.JWTParser;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.Payload;
import com.eatfluencer.eatfluencer.common.PublicKeyNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
@Slf4j
public class KakaoUserService {

	private final UserRepository userRepository;
	private final RestTemplate restTemplate;
	private final JWTParser jwtParser;
	
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
        	if(kid.equals(key.getString("kid"))) {
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
	private boolean isIdTokenValid(String idToken, String kakaoRestApiKey) throws Exception {
		
		DecodedJWT decodedJWT = null;
	    Algorithm algorithm = Algorithm.RSA256(getPublicKeyByKid(idToken));
	    JWTVerifier verifier = JWT.require(algorithm)
	        .withIssuer("https://kauth.kakao.com") // iss가 카카오인지
	        .withAudience(kakaoRestApiKey) // aud가 서비스 앱 키와 일치하는지
	        .build(); // exp는 default로 확인
	    decodedJWT = verifier.verify(idToken);
	    
	    if(decodedJWT == null) {
	    	return false;
	    }
		return true;
		
	}
	
	// 가입 여부 확인
	public boolean isUserSignedUp(JSONObject tokenResponseBody) {
		Optional<User> opt = userRepository.findByKakaoSubject(tokenResponseBody.getString("sub"));
		return opt.isPresent();
	}
	
	// 가입 처리
	// 클라이언트에서 해야 하니까 파라미터를 DTO 같은 걸로?
	public User signUp(JSONObject tokenResponseBody) {
		User newUser = User.builder()
						   .
	}
	
	// 로그인 처리
	public User login(JSONObject tokenResponseBody) {
		
	}
	
	

}
