package com.eatfluencer.eatfluencer_api.auth.jwt;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Table(name = "REFRESH_TOKENS")
public class RefreshToken {

	@Id
	private String refreshToken;
	
	private String userId;
	
	private Date exp;
	
}
