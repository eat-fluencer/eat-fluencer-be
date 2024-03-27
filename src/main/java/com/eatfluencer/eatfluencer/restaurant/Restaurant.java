package com.eatfluencer.eatfluencer.restaurant;

import java.util.ArrayList;
import java.util.List;

import com.eatfluencer.eatfluencer.review.Review;
import com.eatfluencer.eatfluencer.tag.RestaurantTag;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
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
@Table(name = "RESTAURANT")
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RESTAURANT_ID")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Embedded
    @Column(nullable = false)
    private Address address;

    @OneToMany(mappedBy = "restaurant")
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RestaurantTag> restaurantTags = new ArrayList<>();
    
    @Builder
    public Restaurant(String name, Address address) {
    	this.name = name;
    	this.address = address;
    }
    
    public void deleteRestaurantTag(RestaurantTag restaurantTag) {
    	restaurantTag.getTag().getRestaurantTags().remove(restaurantTag); // Tag의 restaurantTags에서도 restaurantTag 삭제
    	this.restaurantTags.remove(restaurantTag);
    }
    
}
