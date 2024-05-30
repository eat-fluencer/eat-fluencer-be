package com.eatfluencer.eatfluencer_api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eatfluencer.eatfluencer_api.entity.OAuth2Provider;
import com.eatfluencer.eatfluencer_api.entity.User;

public interface UserRepository extends JpaRepository<User, Long>{
	public Boolean existsByProviderIdAndProvider(String providerId, OAuth2Provider provider);
	public Optional<User> findByProviderIdAndProvider(String providerId, OAuth2Provider oAuth2Provider);
	public Boolean existsByNickname(String nickname);
}
