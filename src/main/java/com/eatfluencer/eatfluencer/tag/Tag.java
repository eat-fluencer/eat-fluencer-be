package com.eatfluencer.eatfluencer.tag;

import java.util.ArrayList;
import java.util.List;

import com.eatfluencer.eatfluencer.restaurant.RestaurantTag;
import com.eatfluencer.eatfluencer.user.UserTag;

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
@Table(name = "TAG")
public class Tag {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TAG_ID")
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @OneToMany(mappedBy = "tag", fetch = FetchType.LAZY)
    private List<UserTag> userTags = new ArrayList<>();
    
    @OneToMany(mappedBy = "tag", fetch = FetchType.LAZY)
    private List<RestaurantTag> restaurantTags = new ArrayList<>();
    
    @Builder
    public Tag(String name) {
    	this.name = name;
    }
    
    public void updateName(String name) {
    	this.name = name;
    }
    
}