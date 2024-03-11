package com.eatfluencer.eatfluencer.user;

import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.StringTokenizer;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import lombok.RequiredArgsConstructor;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class UserService {

	private final UserRepository userRepository;
	private final RestTemplate restTemplate;
	
	private boolean isPayLoadValid(JSONObject decodedPayload, String kakaoRestApiKey, String nonce) {
		boolean isPayloadValid = true;
		isPayloadValid &= "https://kauth.kakao.com".equals(decodedPayload.getString("iss"));
		isPayloadValid &= kakaoRestApiKey.equals(decodedPayload.getString("aud"));
		isPayloadValid &= decodedPayload.getLong("exp") > System.currentTimeMillis();
		isPayloadValid &= nonce.equals(decodedPayload.getString("nonce"));
		return isPayloadValid;
	}
	
	private boolean isSignatureValid(JSONObject decodedHeader, JSONObject decodedSignature) {
		
		boolean isSignatureValid = false;
		
		// 공개키 목록 조회
		String publicKeyListUrl = "https://kauth.kakao.com/.well-known/jwks.json";
        ResponseEntity<String> response = restTemplate.getForEntity(
        		publicKeyListUrl
        		, String.class);
        JSONObject responseBody = new JSONObject(response.getBody());
        JSONArray keys = new JSONArray(responseBody.getJSONArray("keys"));
        
        // 헤더의 kid 값
        String kid = decodedHeader.getString("kid");
        
        for(JSONObject jwtKey : keys) {
        	if(kid.equals(jwtKey.getString("kid")){
        		 //
        	}
        }
        
        
		return isSignatureValid;
		
	}
	
	public boolean isIdTokenValid(String idToken, String kakaoRestApiKey, String nonce) {
		
		DecodedJWT decodedJWT;
		try {
		    Algorithm algorithm = Algorithm.RSA256(rsaPublicKey, rsaPrivateKey);
		    JWTVerifier verifier = JWT.require(algorithm)
		        // specify any specific claim validations
		        .withIssuer("auth0")
		        // reusable verifier instance
		        .build();
		        
		    decodedJWT = verifier.verify(token);
		} catch (JWTVerificationException exception){
		    // Invalid signature/claims
		}
		
		StringTokenizer tokenizer = new StringTokenizer(idToken, ".");
		Decoder decoder = Base64.getDecoder();
		
		JSONObject decodedHeader = new JSONObject(new String(tokenizer.nextToken()));
		JSONObject decodedPayload = new JSONObject(new String(tokenizer.nextToken()));
		JSONObject decodedSignature = new JSONObject(new String(tokenizer.nextToken()));
		
		boolean isPayloadValid = isPayLoadValid(decodedPayload, kakaoRestApiKey, nonce);
		boolean isSignatureValid = isSignatureValid(decodedHeader, decodedSignature);

		if (!isPayloadValid) {
			return false;
		}
		

		
		
		
		
	}

	private boolean isSignatureValid(JSONObject decodedHeader, JSONObject decodedSignature) {
		// TODO Auto-generated method stub
		return false;
	}
	
	
}
