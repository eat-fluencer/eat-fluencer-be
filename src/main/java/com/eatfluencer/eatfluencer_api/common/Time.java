package com.eatfluencer.eatfluencer_api.common;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
public abstract class Time {
	
	@CreatedDate
	@Column(name = "created_at", updatable = false, nullable = true)
	private LocalDateTime createdAt;
	
	@LastModifiedDate
	@Column(name = "updated_at", updatable = true, nullable = true)
	private LocalDateTime updatedAt;
	
}
