package com.eatfluencer.eatfluencer_api.auth.accesstoken;

import java.util.List;

import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import com.eatfluencer.eatfluencer_api.auth.jwt.JwtProvider;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class AccessTokenAuthenticationProvider implements AuthenticationProvider {

	private final JwtProvider jwtProvider;
	
	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		Object accessTokenObject = authentication.getCredentials();
		if(accessTokenObject == null) {
			throw new AuthenticationCredentialsNotFoundException("Jwt is null.");
		}
		String accessToken = accessTokenObject.toString();
		String userId = jwtProvider.verifyAccessToken(accessToken);
		String role = jwtProvider.getRole(accessToken);
		return new AccessTokenAuthentication(userId, null, List.of(new SimpleGrantedAuthority(role)));
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return AccessTokenAuthentication.class.isAssignableFrom(authentication);
	}

}
