package com.hogumiwarts.lumos.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.hogumiwarts.lumos.jwt.CustomAuthenticationEntryPoint;
import com.hogumiwarts.lumos.jwt.JwtAuthenticationFilter;
import com.hogumiwarts.lumos.jwt.JwtTokenProvider;
import com.hogumiwarts.lumos.redis.RedisTokenService;

/**
 * 각 서비스에서 상속받아 사용할 수 있는 공통 보안 설정
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public abstract class AbstractSecurityConfig {

	private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
	private final JwtTokenProvider jwtTokenProvider;
	private final RedisTokenService redisTokenService;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	/**
	 * 상속받는 클래스에서 permitAll 경로를 설정하도록 추상 메서드 정의
	 */
	protected abstract String[] getPermitAllPaths();

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.csrf(AbstractHttpConfigurer::disable)
			.exceptionHandling(ex -> ex.authenticationEntryPoint(customAuthenticationEntryPoint))
			.authorizeHttpRequests(auth -> auth
				.requestMatchers(
					"/v3/api-docs/**",
					"/swagger-ui/**",
					"/swagger-ui.html"
				).permitAll()
				.requestMatchers(getPermitAllPaths()).permitAll()
				.anyRequest().authenticated()
			)
			.addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider, redisTokenService),
				UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}
}

