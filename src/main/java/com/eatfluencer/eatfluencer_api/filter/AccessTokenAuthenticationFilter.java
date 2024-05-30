package com.eatfluencer.eatfluencer_api.filter;

import java.io.IOException;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.eatfluencer.eatfluencer_api.auth.accesstoken.AccessTokenAuthentication;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class AccessTokenAuthenticationFilter extends OncePerRequestFilter {
	
	private final AuthenticationManager manager;
	
	@Override
	protected void doFilterInternal(
			HttpServletRequest request
		  , HttpServletResponse response
		  , FilterChain filterChain)
			throws ServletException, IOException {

		log.info("AccessTokenAuthenticationFilter starts");
		
		String bearer = request.getHeader("Authorization");
		
		if(bearer == null || !bearer.startsWith("Bearer ")) {
			throw new BadCredentialsException("Missing or invalid access token.");
		}
		
		String accessToken = bearer.substring(7);
		
		Authentication auth = new AccessTokenAuthentication(null, accessToken);
		try {
			auth = manager.authenticate(auth);
			SecurityContextHolder.getContext().setAuthentication(auth);
		} catch(AuthenticationException e) {
			SecurityContextHolder.clearContext();
			request.setAttribute("exception", e.getMessage());
			throw e;
		}
		
		log.info("AccessTokenAuthenticationFilter ends");
		
		filterChain.doFilter(request, response);
		
	}
	
	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
		// 필터
		return (request.getServletPath().equals("/users") && request.getMethod().equals("POST"))
			|| request.getServletPath().equals("/users/token")
			|| request.getServletPath().equals("/users/registration-status")
			|| request.getServletPath().equals("/users/nickname-duplication")
			|| request.getServletPath().equals("/users/refresh-token");
	}
	
}
