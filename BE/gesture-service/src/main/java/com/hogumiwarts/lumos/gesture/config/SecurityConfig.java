package com.hogumiwarts.lumos.gesture.config;

import com.hogumiwarts.lumos.config.AbstractSecurityConfig;
import com.hogumiwarts.lumos.jwt.CustomAuthenticationEntryPoint;
import com.hogumiwarts.lumos.jwt.JwtTokenProvider;
import com.hogumiwarts.lumos.redis.RedisTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig extends AbstractSecurityConfig {

	public SecurityConfig(CustomAuthenticationEntryPoint entryPoint,
						  JwtTokenProvider jwtTokenProvider,
						  RedisTokenService redisTokenService) {
		super(entryPoint, jwtTokenProvider, redisTokenService);
	}

	@Override
	protected String[] getPermitAllPaths() {
		return new String[]{
				"/api/gesture/**",
		};
	}
}