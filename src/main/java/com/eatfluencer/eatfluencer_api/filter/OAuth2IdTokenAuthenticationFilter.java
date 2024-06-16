package com.eatfluencer.eatfluencer_api.filter;

import java.io.IOException;

import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.eatfluencer.eatfluencer_api.auth.oauth2idtoken.OAuth2IdTokenAuthentication;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2IdTokenAuthenticationFilter extends OncePerRequestFilter {
	@Lazy
	private final AuthenticationManager manager;
	
	@Override
	protected void doFilterInternal(
			HttpServletRequest request
		  , HttpServletResponse response
		  , FilterChain filterChain)
			throws ServletException, IOException, AuthenticationException {
		
		log.info("OAuth2IdTokenAuthenticationFilter starts");
		
		// 요청 헤더에서 OAuth2 id token 추출 후 authentication에 삽입
		String idToken = request.getHeader("OAuth2-Id-Token");
		
		Authentication auth = new OAuth2IdTokenAuthentication(null, idToken);
		// 인증
		try {
			Authentication authenticated = manager.authenticate(auth);
			SecurityContextHolder.getContext().setAuthentication(authenticated);
		} catch(AuthenticationException e) {
			SecurityContextHolder.clearContext();
			request.setAttribute("exception", e.getMessage());
			throw e;
		}
		
		log.info("OAuth2IdTokenAuthenticationFilter ends");
		
		filterChain.doFilter(request, response);

		
	}
	
	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
		// 회원가입, 토큰 발급만 필터
		return !(request.getServletPath().equals("/users") && request.getMethod().equals("POST"))
			&& !(request.getServletPath().equals("/users/token") && request.getMethod().equals("GET"));
	}

}
