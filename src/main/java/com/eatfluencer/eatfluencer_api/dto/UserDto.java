package com.eatfluencer.eatfluencer_api.dto;

import com.eatfluencer.eatfluencer_api.auth.oauth2idtoken.OAuth2IdClass;
import com.eatfluencer.eatfluencer_api.entity.OAuth2Provider;
import com.eatfluencer.eatfluencer_api.entity.Role;
import com.eatfluencer.eatfluencer_api.entity.User;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class UserDto {

	private String userId;
	private String providerId;
    private OAuth2Provider provider;
    private String email;
    private String nickname;
    private String picture;
    private Role role;

    @Builder
	public UserDto(Long userId, String providerId, String provider, String email, String nickname, String picture, String role) {
		this.userId = userId.toString();
    	this.providerId = providerId;
		this.provider = Enum.valueOf(OAuth2Provider.class, provider.toUpperCase());
		this.email = email;
		this.nickname = nickname;
		this.picture = picture;
		this.role = Enum.valueOf(Role.class, role.toUpperCase());
	}
    
	public User toEntity() {
		return User.builder()
				   .providerId(providerId)
				   .provider(provider)
				   .email(email)
				   .nickname(nickname)
				   .picture(picture)
				   .role(role)
				   .build();
	}
	
	public OAuth2IdClass getOAuth2IdClass() {
		return new OAuth2IdClass(providerId, provider);
	}
	
}
