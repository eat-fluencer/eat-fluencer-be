package com.eatfluencer.eatfluencer_api.auth.oauth2idtoken;

import com.eatfluencer.eatfluencer_api.entity.OAuth2Provider;

import lombok.Getter;

@Getter
public class OAuth2IdClass {
	private String providerId;
	private OAuth2Provider provider;
	
	public OAuth2IdClass(String providerId, String providerUrl) {
		this.providerId = providerId;
		this.provider = OAuth2Provider.valueByUrl(providerUrl);
	}
	
	public OAuth2IdClass(String providerId, OAuth2Provider provider) {
		this.providerId = providerId;
		this.provider = provider;
	}
	
}
