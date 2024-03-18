package com.eatfluencer.eatfluencer.user.dto;

import java.util.List;

import com.eatfluencer.eatfluencer.tag.Tag;
import com.eatfluencer.eatfluencer.user.User;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class KakaoSignUpRequestDto {
	private String nickname;
	private String email;
	private String subject;
	private String picture;
	private List<Tag> tags;
	
	public User toEntity() {
		return User.builder()
				   .nickname(this.getNickname())
				   .subject(this.getSubject())
				   .picture(this.getPicture())
				   .build();
	}
	
}
