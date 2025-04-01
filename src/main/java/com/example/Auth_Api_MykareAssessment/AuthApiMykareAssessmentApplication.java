package com.example.Auth_Api_MykareAssessment;

import Config.EnvConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {"Controller", "Service", "Repository", "Config"})
@EntityScan(basePackages = "Entity")
@EnableJpaRepositories(basePackages = "Repository")
public class AuthApiMykareAssessmentApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthApiMykareAssessmentApplication.class, args);

	}
}
