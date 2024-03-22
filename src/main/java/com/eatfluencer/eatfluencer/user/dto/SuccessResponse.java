package com.eatfluencer.eatfluencer.user.dto;

import java.util.Map;

import org.springframework.http.HttpStatus;

import lombok.Builder;
import lombok.Getter;

@Getter
public class SuccessResponse<T> {

	private boolean success = true;
	private int status;
	private Map<String, T> data;
	
	@Builder
	public SuccessResponse(HttpStatus httpStatus, Map<String, T> data) {
		this.status = httpStatus.value();
		this.data = data;
	}
	
}
