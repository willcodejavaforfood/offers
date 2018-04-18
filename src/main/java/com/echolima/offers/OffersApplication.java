package com.echolima.offers;

import com.google.common.collect.Maps;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class OffersApplication {

	public static void main(String[] args) {
		SpringApplication.run(OffersApplication.class, args);
	}

	@Bean
	public OffersRepository offersRepository(TimeService timeService) {
		return new OffersRepository(timeService, Maps.newHashMap());
	}
}
