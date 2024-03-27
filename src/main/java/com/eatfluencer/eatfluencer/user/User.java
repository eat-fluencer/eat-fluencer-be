package com.eatfluencer.eatfluencer.user;


import java.util.ArrayList;
import java.util.List;

import com.eatfluencer.eatfluencer.comment.Comment;
import com.eatfluencer.eatfluencer.common.Time;
import com.eatfluencer.eatfluencer.review.Review;
import com.eatfluencer.eatfluencer.tag.UserTag;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "USERS")
public class User extends Time {
    
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true)
    private String providerId;
    
    private String provider;
    
    @Column(unique = true, nullable = true)
    private String email;
    
    private String nickname;
    
    private String picture;
    
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();
    
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();
    
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Favorite> favorites = new ArrayList<>();
    
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserTag> userTags = new ArrayList<>();
    
    @Builder
    public User(String providerId, String provider, String email, String nickname, String picture) {
    	this.providerId = providerId;
    	this.provider = provider;
    	this.email = email;
    	this.nickname = nickname;
    	this.picture = picture;
    }
    
    public void setNickname(String nickname) {
    	this.nickname = nickname;
    }
    
    public void deleteReview(Review review) {
    	review.getRestaurant().getReviews().remove(review); // Restaurant의 reviews에서도 review 삭제
    	this.reviews.remove(review);
    }
    
    public void deleteComment(Comment comment) {
    	comment.getReview().getComments().remove(comment); // Review의 comments에서도 comment 삭제
    	this.comments.remove(comment);
    }
    
    public void deleteFavorite(Favorite favorite) {
    	this.favorites.remove(favorite);
    }
    
    public void deleteUserTag(UserTag userTag) {
    	userTag.getTag().getUserTags().remove(userTag); // Tag의 userTags에서도 userTag 삭제
    	this.userTags.remove(userTag);
    }
    
}
