package com.eatfluencer.eatfluencer_api.auth.accesstoken;

import java.util.Collection;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

public class AccessTokenAuthentication extends UsernamePasswordAuthenticationToken{
	
	private static final long serialVersionUID = -1898445926425607332L;

	public AccessTokenAuthentication(
			String userId
		  , String accessToken
		  , Collection<? extends GrantedAuthority> authorities) {
		super(userId, null, authorities);
	}
	
	public AccessTokenAuthentication(
			Object principal
		  , String accessToken) {
		super(null, accessToken);
	}
	
}
