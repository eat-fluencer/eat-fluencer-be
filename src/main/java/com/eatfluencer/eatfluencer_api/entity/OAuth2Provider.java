package com.eatfluencer.eatfluencer_api.entity;

import lombok.Getter;

@Getter
public enum OAuth2Provider {
	
	KAKAO("https://kauth.kakao.com");
	
	private String url;

	OAuth2Provider(String url) {
		this.url = url;
	}
	
	public static OAuth2Provider valueByUrl(String url) {
		for(OAuth2Provider provider : OAuth2Provider.values()) {
			if(url.equals(provider.getUrl())) {
				return provider;
			}
		}
		return null;
	}
	
}
