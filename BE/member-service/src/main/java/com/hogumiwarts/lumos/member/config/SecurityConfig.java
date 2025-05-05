package com.hogumiwarts.lumos.member.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.csrf(AbstractHttpConfigurer::disable)
			.authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
		return http.build();
	}

	// @Bean
	// public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
	// 	http
	// 		.csrf(AbstractHttpConfigurer::disable) // CSRF 비활성화
	// 		.authorizeHttpRequests(auth -> auth
	// 			.requestMatchers(
	// 				"/gesture/v3/api-docs/**",       // API 문서 JSON
	// 				"/gesture/swagger-ui/**",        // Swagger 리소스들
	// 				"/gesture/swagger-ui.html",      // Swagger 진입점
	// 				"/v3/api-docs/**",               // 혹시 내부적으로 이 경로로도 쓰일 수 있으니 함께 허용
	// 				"/swagger-ui/**",                // fallback 대비
	// 				"/swagger-ui.html",
	// 				"/gesture/api/sensor"
	// 			).permitAll()
	// 			.anyRequest().authenticated()
	// 		);
	//
	// 	return http.build();
	// }
}
