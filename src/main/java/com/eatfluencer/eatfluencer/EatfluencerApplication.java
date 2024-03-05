package com.eatfluencer.eatfluencer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class EatfluencerApplication {

	public static void main(String[] args) {
		SpringApplication.run(EatfluencerApplication.class, args);
	}

}
