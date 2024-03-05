package com.eatfluencer.eatfluencer.review;

import java.util.ArrayList;
import java.util.List;

import com.eatfluencer.eatfluencer.comment.Comment;
import com.eatfluencer.eatfluencer.common.Time;
import com.eatfluencer.eatfluencer.image.Image;
import com.eatfluencer.eatfluencer.restaurant.Restaurant;
import com.eatfluencer.eatfluencer.user.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "REVIEW")
public class Review extends Time {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "REVIEW_ID")
    private Long id;
    
    private String title;
    private String content;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "USER_ID")
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "RESTAURANT_ID")
    private Restaurant restaurant;
    
    @OneToMany(mappedBy = "review", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Image> images = new ArrayList<>();
    
    @OneToMany(mappedBy = "review", fetch = FetchType.LAZY)
    private List<Comment> comments = new ArrayList<>();
    
    @Builder
    public Review(String title, String content, User user, Restaurant restaurant) {
    	
    	this.title = title;
    	this.content = content;
    	this.restaurant = restaurant;
    	
    	// 양방향 연관관계
    	this.user = user;
    	user.getReviews().add(this);
    	
    }
    
    public void deleteImage(Image image) {
    	images.remove(image);
    }
    
    public void deleteComment(Comment comment) {
    	comments.remove(comment);
    }
    
}
