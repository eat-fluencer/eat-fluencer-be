package com.eatfluencer.eatfluencer_api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.eatfluencer.eatfluencer_api.auth.exceptionhandler.CustomAuthenticationEntryPoint;
import com.eatfluencer.eatfluencer_api.filter.AccessTokenAuthenticationFilter;
import com.eatfluencer.eatfluencer_api.filter.OAuth2IdTokenAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
	
	private final OAuth2IdTokenAuthenticationFilter oAuth2IdTokenAuthenticationFilter;
	private final AccessTokenAuthenticationFilter accessTokenAuthenticationFilter;
	private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    	
    	http.csrf(csrf ->
    			csrf.disable()
    		);
    	
    	http.addFilterBefore(oAuth2IdTokenAuthenticationFilter
    					, UsernamePasswordAuthenticationFilter.class)
    		.addFilterAfter(accessTokenAuthenticationFilter
    						, UsernamePasswordAuthenticationFilter.class)
    		.exceptionHandling(exceptionHandling ->
    				exceptionHandling.authenticationEntryPoint(customAuthenticationEntryPoint));
    	
    	http.authorizeHttpRequests(authorizeHttpRequests ->
				authorizeHttpRequests
				.requestMatchers("/users/registration-status", "/users/nickname-duplication").permitAll()
				.requestMatchers(HttpMethod.POST, "/users").permitAll()
				.requestMatchers(HttpMethod.GET, "/users/token").permitAll()
				.requestMatchers("/users/refresh-token").permitAll()
				.anyRequest().authenticated()
    		)
    		.headers(headers ->
    			headers.frameOptions(FrameOptionsConfig::sameOrigin)
    		);
    	
    	return http.build();
    	
    }

}
