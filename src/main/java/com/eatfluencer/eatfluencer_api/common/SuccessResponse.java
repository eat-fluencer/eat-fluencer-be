package com.eatfluencer.eatfluencer_api.common;

import java.util.Map;

import org.springframework.http.HttpStatus;

import lombok.Builder;
import lombok.Getter;

@Getter
public class SuccessResponse {

	private boolean success = true;
	private int status;
	private Map<String, Object> data;
	
	@Builder
	public SuccessResponse(HttpStatus httpStatus, Map<String, Object> data) {
		this.status = httpStatus.value();
		this.data = data;
	}
	
}
