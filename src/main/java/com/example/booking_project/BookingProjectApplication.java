package com.example.booking_project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication

@ComponentScan(basePackages = "com.example.booking_project.*", excludeFilters = {
		@ComponentScan.Filter(type = FilterType.REGEX, pattern = {}) })
@EnableJpaAuditing
@EnableScheduling
public class BookingProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookingProjectApplication.class, args);
	}

}
