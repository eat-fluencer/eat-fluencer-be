package com.eatfluencer.eatfluencer.provider;

import org.json.JSONObject;

import com.eatfluencer.eatfluencer.user.User;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class KakaoUserInfo implements OAuth2UserInfo {
	
	private String providerId;
	private final String provider = "kakao";
//	private String email;
	private String nickname;
	private String picture;
	
	public KakaoUserInfo(JSONObject jsonObject) {
		providerId = jsonObject.getString("sub");
//		email = jsonObject.getString("email");
		nickname = jsonObject.getString("nickname");
		picture = jsonObject.getString("picture");
	}
	
	public User toEntity() {
		return User.builder()
				   .providerId(providerId)
				   .provider(provider)
//				   .email(email)
				   .nickname(nickname)
				   .picture(picture)
				   .build();
	}
	
}
