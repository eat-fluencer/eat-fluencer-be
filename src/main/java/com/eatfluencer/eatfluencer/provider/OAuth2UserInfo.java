package com.eatfluencer.eatfluencer.provider;

import com.eatfluencer.eatfluencer.user.User;

public interface OAuth2UserInfo {
	public User toEntity();
}
