package com.expensesEye.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * This Spring Configuration class, is used for starting the application using
 * Spring Boot
 */
@SpringBootApplication(scanBasePackages = { "com.expensesEye.rest.controller", "com.expensesEye.persist",
		"com.expensesEye.service", "com.expensesEye.security", "com.expensesEye.model" })
@EntityScan(basePackages = "com.expensesEye.persist.entity")
public class SpringBootConfig extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(new Class[] { SpringBootConfig.class, WebConfig.class, SecurityConfig.class,
				WebSecurityConfiguration.class, MethodSecurityConfig.class }, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(SpringBootConfig.class);
	}

	/*
	 * Beans
	 */
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
