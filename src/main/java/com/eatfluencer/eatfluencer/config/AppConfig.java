package com.eatfluencer.eatfluencer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import com.auth0.jwt.impl.JWTParser;

@Configuration
public class AppConfig {
	
	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
	
	@Bean JWTParser jwtParser() {
		return new JWTParser();
	}
	
}
