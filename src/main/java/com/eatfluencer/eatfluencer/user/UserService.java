package com.eatfluencer.eatfluencer.user;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.auth0.jwt.impl.JWTParser;
import com.eatfluencer.eatfluencer.exception.TokenNotFoundException;
import com.eatfluencer.eatfluencer.exception.UserNotFoundException;
import com.eatfluencer.eatfluencer.provider.OAuth2UserInfo;
import com.eatfluencer.eatfluencer.user.dto.ErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
@Slf4j
public class UserService {

	private final UserRepository userRepository;
	private final JWTParser jwtParser;
	
	// 가입 여부 확인
	public Boolean checkUserSignedUp(String providerId, String provider) throws UserNotFoundException {
		
		Boolean isUserSignedUp = userRepository.existsByProviderIdAndProvider(providerId, provider);
		
		if(isUserSignedUp) {
			return true;
		} else {
			throw new UserNotFoundException("no user with " + provider + " ID: " + providerId
					, ErrorCode.USER_NOT_FOUND);
		}
															
		
	}
    
    // HTTP Header에서 token 추출
    public String extractTokenFromHttpHeader(String requestHeader) {
    	String token = null;
    	if(requestHeader != null && requestHeader.startsWith("Bearer ")) {
    		token =  requestHeader.substring(7);
    	} else {
    		throw new TokenNotFoundException("token not found", ErrorCode.TOKEN_NOT_FOUND);
    	}
    	return token;
    }
	
	// 가입 처리
	@Transactional
	public User signUpUser(OAuth2UserInfo request) {
		User addUser = userRepository.save(request.toEntity());
		//List<Tag> tags = request.getTags();
		//tags.stream()
		//	.forEach(tag -> userTagRepository.save(new UserTag(addUser, tag)));
		return addUser;
	}
	
	// 회원탈퇴 처리
	@Transactional
	public User cancelUser(String providerId, String provider) throws UserNotFoundException {
		
		// 사용자 조회
		User deleteUser = userRepository.findByProviderIdAndProvider(providerId, provider)
												  .orElseThrow(() -> new UserNotFoundException("user not found with " +  provider + " ID: " + providerId
															 , ErrorCode.USER_NOT_FOUND));  
		
        // 사용자 삭제
        userRepository.delete(deleteUser);
            
        return deleteUser;
		
	} 

}
