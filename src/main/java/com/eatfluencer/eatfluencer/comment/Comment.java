package com.eatfluencer.eatfluencer.comment;

import com.eatfluencer.eatfluencer.common.Time;
import com.eatfluencer.eatfluencer.review.Review;
import com.eatfluencer.eatfluencer.user.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "COMMENT")
public class Comment extends Time {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "COMMENT_ID")
    private Long id;
    
    private String content;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "REVIEW_ID")
    private Review review;
    
    @Builder
    public Comment(String content, User user, Review review) {
    	
    	this.content = content;
    	
    	// 양방향 연관관계
    	this.user = user;
    	user.getComments().add(this);
    	
    	this.review = review;
    	review.getComments().add(this);
    	
    }
    
}
