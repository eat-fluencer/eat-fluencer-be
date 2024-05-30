package com.eatfluencer.eatfluencer_api.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eatfluencer.eatfluencer_api.auth.jwt.JwtProvider;
import com.eatfluencer.eatfluencer_api.auth.oauth2idtoken.OAuth2IdClass;
import com.eatfluencer.eatfluencer_api.dto.UserDto;
import com.eatfluencer.eatfluencer_api.entity.OAuth2Provider;
import com.eatfluencer.eatfluencer_api.entity.User;
import com.eatfluencer.eatfluencer_api.repository.UserRepository;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
@Slf4j
public class UserService {

	private final UserRepository userRepository;
	private final JwtProvider jwtProvider;
	
	// 가입 여부 확인
	public Boolean checkUserSignedUp(Map<String, String> map) {

		String providerId = map.get("provider_id");
		String provider = map.get("provider");
		
		return userRepository.existsByProviderIdAndProvider(providerId, Enum.valueOf(OAuth2Provider.class, provider.toUpperCase()));
		
	}
	
	// 닉네임 중복 체크
	public Boolean isNicknameDuplicated(String nickname) {
		return userRepository.existsByNickname(nickname);
	}
	
	// 서비스 회원 정보 불러오기
	public UserDto getUserInfo(String userId) throws EntityNotFoundException {
		return userRepository.findById(Long.parseLong(userId))
					.orElseThrow(() -> new EntityNotFoundException("User not found."))
					.toDto();
	}
	
	public List<User> findAllUsers() {
		return userRepository.findAll();
	}
	
	// 가입 처리
	@Transactional
	public User signUpUser(UserDto userDto) throws EntityExistsException {
		
		Optional<User> optUser = userRepository.findByProviderIdAndProvider(
				userDto.getProviderId(), userDto.getProvider());
		
		if(optUser.isEmpty()) {
			return userRepository.save(userDto.toEntity());
		} else {
			throw new EntityExistsException("Duplicate User");
		}
		
	}
	
	// 로그인
	@Transactional
	public Map<String, Object> loginUser(OAuth2IdClass idClass) throws UsernameNotFoundException {
		
		User loginUser = userRepository.findByProviderIdAndProvider(idClass.getProviderId(), idClass.getProvider())
				.orElseThrow(() -> new UsernameNotFoundException("User not found"));  
		
		return jwtProvider.createTokens(loginUser.getId(), loginUser.getRole());
		
	}

	// 리프레시 토큰으로 재로그인
	@Transactional
	public Map<String, Object> refresh(String refreshToken) throws UsernameNotFoundException {
		
		String userId = jwtProvider.verifyRefreshToken(refreshToken);
		User refreshTokenUser = userRepository.findById(Long.parseLong(userId))
				.orElseThrow(() -> new UsernameNotFoundException("User not found")); 
		
		return jwtProvider.regenerateTokens(refreshTokenUser.getId(), refreshTokenUser.getRole(), refreshToken);
	}

	public String logoutUser(String userId) {
		jwtProvider.deleteRefreshToken(userId);
		return userId;
	} 

	// 회원탈퇴 처리
	@Transactional
	public User cancelUser(String userId) throws UsernameNotFoundException {
		
		// 사용자 조회
		User deleteUser = userRepository.findById(Long.parseLong(userId))
				.orElseThrow(() -> new UsernameNotFoundException("user not found with ID: " + userId));  
		
		// 사용자 삭제
		userRepository.delete(deleteUser);
		
		return deleteUser;
		
	}
	
}
