package com.eatfluencer.eatfluencer_api.auth.jwt;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
	public void deleteByUserId(String userId);
}
