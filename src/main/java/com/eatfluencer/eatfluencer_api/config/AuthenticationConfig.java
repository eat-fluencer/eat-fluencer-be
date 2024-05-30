package com.eatfluencer.eatfluencer_api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import com.eatfluencer.eatfluencer_api.auth.accesstoken.AccessTokenAuthenticationProvider;
import com.eatfluencer.eatfluencer_api.auth.oauth2idtoken.OAuth2IdTokenAuthenticationProvider;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class AuthenticationConfig {

	private final AccessTokenAuthenticationProvider accessTokenAuthenticationProvider;
	private final OAuth2IdTokenAuthenticationProvider oauth2IdTokenAuthenticationProvider;
	
    @Bean
    AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
    	
    	AuthenticationManagerBuilder authenticationManagerBuilder = 
                http.getSharedObject(AuthenticationManagerBuilder.class);
    	
        authenticationManagerBuilder
        	.authenticationProvider(accessTokenAuthenticationProvider)
        	.authenticationProvider(oauth2IdTokenAuthenticationProvider)
        	.parentAuthenticationManager(null);
        
        AuthenticationManager authenticationManager = authenticationManagerBuilder.build();
        http.authenticationManager(authenticationManager);
        
        return authenticationManager;
        
    }
    
}
