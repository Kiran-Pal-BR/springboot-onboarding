package com.example.onboarding;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootApplication
public class OnboardingApplication {

	private static final Logger log = LoggerFactory.getLogger(OnboardingApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(OnboardingApplication.class, args);
		log.info("Spring Boot Onboarding Application successfully started!");
	}
}