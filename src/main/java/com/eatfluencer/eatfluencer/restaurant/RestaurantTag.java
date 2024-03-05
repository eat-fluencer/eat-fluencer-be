package com.eatfluencer.eatfluencer.restaurant;

import com.eatfluencer.eatfluencer.tag.Tag;

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
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "RESTAURANT_TAG")
public class RestaurantTag {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RESTAURANT_TAG_ID")
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "RESTAURANT_ID")
    private Restaurant restaurant;
    
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "TAG_ID")
    private Tag tag;

    @Builder
    public RestaurantTag(Restaurant restaurant, Tag tag) {

        // 양방향 연관관계
        this.restaurant = restaurant;
        restaurant.getRestaurantTags().add(this);
        
        this.tag = tag;
        tag.getRestaurantTags().add(this);
        
    }
    
}
