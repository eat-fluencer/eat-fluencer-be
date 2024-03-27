package com.eatfluencer.eatfluencer.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Builder
@AllArgsConstructor
@Getter
public class TokenResponseDto {
	private String idToken;
	private String accessToken;
	private int expiresIn;
}
