package com.example.onboarding;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@SpringBootApplication
@OpenAPIDefinition(
		info = @Info(
				title = "Enterprise Onboarding & OAuth2 Resource Server API",
				version = "v1.0",
				description = "Production-ready Spring Boot application implementing secure OAuth2 JWT authentication, role-based access control, Actuator health monitoring, and OpenAPI documentation."
		)
)
public class OnboardingApplication {

	private static final Logger log = LoggerFactory.getLogger(OnboardingApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(OnboardingApplication.class, args);
		log.info("Spring Boot Onboarding Application successfully started!");
	}
}