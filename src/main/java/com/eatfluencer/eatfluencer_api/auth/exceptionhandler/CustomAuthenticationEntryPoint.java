package com.eatfluencer.eatfluencer_api.auth.exceptionhandler;

import java.io.IOException;

import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.eatfluencer.eatfluencer_api.common.ErrorResponse;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {
		
		Object messageObject = request.getAttribute("exception");
		String message = messageObject != null ? messageObject.toString() : authException.toString();
		
		ErrorResponse errorResponse = new ErrorResponse(HttpStatus.UNAUTHORIZED, message);
		
		response.setStatus(HttpStatus.UNAUTHORIZED.value());
		response.setContentType("application/json");
		response.getWriter().write(new JSONObject(errorResponse).toString());
		return;
		
	}

}
