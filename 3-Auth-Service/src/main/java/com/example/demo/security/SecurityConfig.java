package com.example.demo.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.Customizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

	    http
	        .csrf(csrf -> csrf.disable())
	        .authorizeHttpRequests(auth -> auth
	            .requestMatchers(
	                "/auth/**",
	                "/swagger-ui.html",
	                "/swagger-ui/**",
	                "/v3/api-docs/**",
	                "/v3/api-docs",
	                "/swagger-ui/index.html"
	            ).permitAll()
	            .anyRequest().authenticated()
	        )
	        .httpBasic(httpBasic -> httpBasic.disable());   // 👈 IMPORTANT

	    return http.build();
	}}