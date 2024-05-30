package com.eatfluencer.eatfluencer_api.auth.oauth2idtoken;

import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import com.eatfluencer.eatfluencer_api.auth.jwt.JwtProvider;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class OAuth2IdTokenAuthenticationProvider implements AuthenticationProvider {

	private final JwtProvider jwtProvider;
	
	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		
		OAuth2IdClass oauth2IdClass = null;
		
		try {
			Object idTokenObject = authentication.getCredentials();
			if(idTokenObject == null) {
				throw new AuthenticationCredentialsNotFoundException("Jwt is null.");
			}
			String idToken = idTokenObject.toString();
			oauth2IdClass = jwtProvider.getOAuth2IdClass(idToken);
			jwtProvider.verifyIdToken(idToken);			
		} catch(ExpiredJwtException e) {
			throw new CredentialsExpiredException("Jwt has been expired.");
		} catch(MalformedJwtException | UnsupportedJwtException e) {
			throw new BadCredentialsException("Malformed/Unsupported jwt.");
		} catch(SignatureException e) {
			throw new BadCredentialsException("Signature is not valid.");
		} catch(JwtException e) {
			throw new BadCredentialsException("Something wrong in Jwt.");
		} catch(Exception e) {
			throw new BadCredentialsException(e.getMessage());
		}
		
		return new OAuth2IdTokenAuthentication(oauth2IdClass, null, null);
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return OAuth2IdTokenAuthentication.class.isAssignableFrom(authentication);
	}

}
