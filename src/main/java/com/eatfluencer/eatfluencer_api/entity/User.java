package com.eatfluencer.eatfluencer_api.entity;


import com.eatfluencer.eatfluencer_api.common.Time;
import com.eatfluencer.eatfluencer_api.dto.UserDto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "USERS")
public class User extends Time {
    
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true)
    private String providerId;
    
    @Enumerated(EnumType.STRING)
    private OAuth2Provider provider;
    
    @Column(unique = true, nullable = true)
    private String email;
    
    @Column(unique = true, nullable = false)
    private String nickname;
    
    private String picture;
    
    @Enumerated(EnumType.STRING)
    private Role role;
    
    @Builder
    public User(String providerId, OAuth2Provider provider, String email, String nickname, String picture, Role role) {
    	this.providerId = providerId;
    	this.provider = provider;
    	this.email = email;
    	this.nickname = nickname;
    	this.picture = picture;
    	this.role = role;
    }
    
    public void setNickname(String nickname) {
    	this.nickname = nickname;
    }

	public UserDto toDto() {
		return UserDto.builder()
					  .userId(id)
					  .providerId(providerId)
					  .provider(provider.toString())
					  .email(email)
					  .nickname(nickname)
					  .picture(picture)
					  .role(role.toString())
					  .build();
	}
    
}
