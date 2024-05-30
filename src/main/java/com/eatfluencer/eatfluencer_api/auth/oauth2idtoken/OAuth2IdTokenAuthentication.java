package com.eatfluencer.eatfluencer_api.auth.oauth2idtoken;

import java.util.Collection;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

public class OAuth2IdTokenAuthentication extends UsernamePasswordAuthenticationToken{
	
	private static final long serialVersionUID = -1898445926425607332L;

	public OAuth2IdTokenAuthentication(
		    Object principal
		  , String idToken
		  , Collection<? extends GrantedAuthority> authorities) {
		super(principal, null, null);
	}
	
	public OAuth2IdTokenAuthentication(
			Object principal
		  , String idToken) {
		super(null, idToken);
	}
	
	@Override
	public void eraseCredentials() {
		
	}
	
}
