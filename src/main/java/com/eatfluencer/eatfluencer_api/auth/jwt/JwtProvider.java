package com.eatfluencer.eatfluencer_api.auth.jwt;


import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

import javax.crypto.SecretKey;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.eatfluencer.eatfluencer_api.auth.oauth2idtoken.OAuth2IdClass;
import com.eatfluencer.eatfluencer_api.entity.Role;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtProvider {
	
	@Value("${jwt.secretkey.access}")
    private String accessTokenSecretKey;
    
    @Value("${jwt.secretkey.refresh}")
    private String refreshTokenSecretKey;
    
    private final long ACCESS_TOKEN_TTL = 999999999999L;// 1 * 60 * 60 * 1000; // 1시간
    private final long REFRESH_TOKEN_TTL = 999999999999L; // 14일
    
    private final RestTemplate restTemplate;
    private final RefreshTokenRepository refreshTokenRepository;
    
	private String createAccessToken(Long userId, Role role)  {
		
		SecretKey secretKey = Keys.hmacShaKeyFor(accessTokenSecretKey.getBytes(
											StandardCharsets.UTF_8));
		
		return Jwts.builder()
					.setIssuer("nextor.ai")
					.setSubject(userId.toString())
					.claim("role", role.toString())
					.setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_TTL))
					.signWith(secretKey)
					.compact();
	    
	}
	
	private String createRefreshToken(Long userId) {
		
		Date exp = new Date(System.currentTimeMillis() + REFRESH_TOKEN_TTL);
		
		SecretKey secretKey = Keys.hmacShaKeyFor(accessTokenSecretKey.getBytes(
				StandardCharsets.UTF_8));

		String refreshToken = Jwts.builder()
								.setIssuer("nextor.ai")
								.setSubject(userId.toString())
								.setExpiration(exp)
								.signWith(secretKey)
								.compact();
		
		refreshTokenRepository.save(new RefreshToken(refreshToken, userId.toString(), exp));
		
		return refreshToken;
		
	}
	
	public void expireRefreshToken(String userId) {
		refreshTokenRepository.deleteByUserId(userId);
	}
	
	public Map<String, Object> createTokens(Long userId, Role role) {
		return Map.of("accessToken", createAccessToken(userId, role)
					, "refreshToken", createRefreshToken(userId)
					, "accessTokenMaxAge", ACCESS_TOKEN_TTL
					, "refreshTokenMaxAge", REFRESH_TOKEN_TTL);
	}
	
	public Map<String, Object> regenerateTokens(Long userId, Role role, String refreshToken) {
		
		// 기존 리프레시 토큰 DB에서 삭제
		refreshTokenRepository.deleteById(refreshToken);
		
		return Map.of("accessToken", createAccessToken(userId, role)
					, "refreshToken", createRefreshToken(userId)
					, "accessTokenMaxAge", ACCESS_TOKEN_TTL
					, "refreshTokenMaxAge", REFRESH_TOKEN_TTL);
		
	}
	
	public String verifyAccessToken(String accessToken) {
		
		String userId = null;
		
		try {

			SecretKey secretKey = Keys.hmacShaKeyFor(accessTokenSecretKey.getBytes());
			
			userId = Jwts.parserBuilder()
						.setSigningKey(secretKey)
						.requireIssuer("nextor.ai")
						.build()
						.parseClaimsJws(accessToken)
						.getBody()
						.getSubject(); // exp는 default로 확인
			
		} catch(ExpiredJwtException e) {
			throw new CredentialsExpiredException("Access token has expired.");
		} catch(JwtException e) {
			log.error(e.getLocalizedMessage());
			throw new BadCredentialsException("Invalid access token.");
		} catch(Exception e) {
			throw new RuntimeException("Unexpected error during access token validation");
		}
		
		return userId;

	}
	
	public String verifyRefreshToken(String refreshToken) {
		
		String userId = null;
		SecretKey secretKey = Keys.hmacShaKeyFor(refreshTokenSecretKey.getBytes());
		
		try {
			Jwts.parserBuilder()
				.setSigningKey(secretKey)
				.requireIssuer("nextor.ai")
				.build()
				.parseClaimsJws(refreshToken);
			
			refreshTokenRepository.findById(refreshToken)
			.orElseThrow(() -> new ExpiredJwtException(null, null, "Refresh token is not found in DB."));
			
		} catch(ExpiredJwtException e) {
			throw new CredentialsExpiredException("Refresh token has been expired.");
		} catch(JwtException e) {
			throw new BadCredentialsException("Invalid refresh token.");
		} catch(Exception e) {
			throw new BadCredentialsException("Unexpected error during refresh token validation");
		}
		
		return userId;
		
	}
	
	// 카카오 공개 키 kid로 검색 후 RSAPublicKey로 만들어 반환
	private PublicKey getPublicKey(String kid) throws Exception {
		
		// 공개키 목록 조회
		String publicKeyListUrl = "https://kauth.kakao.com/.well-known/jwks.json";
		String responseBodyAsString = restTemplate.getForObject(
						        		publicKeyListUrl
						        		, String.class);
		
		JSONObject responseBody = new JSONObject(responseBodyAsString);
        JSONArray keys = responseBody.getJSONArray("keys");
        
        for(int i = 0; i < keys.length(); i++) {
        	JSONObject key = keys.getJSONObject(i);
        	if(kid.equals(key.getString("kid"))) { // 특정 kid를 가진 key 찾으면 퍼블릭키 생성 후 반환
        		 BigInteger modulus = new BigInteger(1, Base64.getUrlDecoder().decode(key.getString("n")));
                 BigInteger exponent = new BigInteger(1, Base64.getUrlDecoder().decode(key.getString("e")));
                 RSAPublicKeySpec spec = new RSAPublicKeySpec(modulus, exponent);
                 KeyFactory factory = KeyFactory.getInstance(key.getString("kty")); // 카카오에서 "kty" : "RSA"로 고정
                 return factory.generatePublic(spec);
        	}
        }
        
        // 특정 kid를 가진 key가 없으면 예외처리 
		throw new RuntimeException("Public key not found for the kid: " + kid);
        
	}
	
	// id_token 검증
	public void verifyIdToken(String idToken) throws Exception {
		
		String[] parts = idToken.split("\\.");
        if (parts.length < 2) {
            throw new IllegalArgumentException("Invalid JWT token");
        }

        String header = new String(Decoders.BASE64URL.decode(parts[0]));

        JSONObject headerJson = new JSONObject(header);

        String kid = headerJson.getString("kid");

		PublicKey publicKey = getPublicKey(kid);
		Jwts.parserBuilder()
			.setSigningKey(publicKey)
			.requireIssuer("https://kauth.kakao.com") // iss가 카카오인지
			.requireAudience(kakaoServiceappKey) // aud가 서비스 앱 키와 일치하는지
			.build()
			.parseClaimsJws(idToken); // exp는 default로 확인
		
	}

	public void deleteRefreshToken(String userId) {
		refreshTokenRepository.deleteByUserId(userId);
	}

	public OAuth2IdClass getOAuth2IdClass(String idToken) {
		
		String[] parts = idToken.split("\\.");
        if (parts.length < 2) {
            throw new IllegalArgumentException("Invalid JWT token");
        }
        
        String payload = new String(Decoders.BASE64URL.decode(parts[1]));
        JSONObject payloadJson = new JSONObject(payload);
        String providerId = payloadJson.getString("sub");
        String provider = payloadJson.getString("iss");
        
        return new OAuth2IdClass(providerId, provider);

	}

	public String getRole(String accessToken) {
		String[] parts = accessToken.split("\\.");
        if (parts.length < 2) {
            throw new IllegalArgumentException("Invalid JWT token");
        }
        
        String payload = new String(Decoders.BASE64URL.decode(parts[1]));
        JSONObject payloadJson = new JSONObject(payload);
        String role = payloadJson.getString("role");
        
        return role;
	}
	
}
