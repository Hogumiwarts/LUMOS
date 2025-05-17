package com.hogumiwarts.lumos.gesture.config;

import com.hogumiwarts.lumos.config.AbstractSecurityConfig;
import com.hogumiwarts.lumos.jwt.CustomAuthenticationEntryPoint;
import com.hogumiwarts.lumos.jwt.JwtTokenProvider;
import com.hogumiwarts.lumos.redis.RedisTokenService;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecurityConfig extends AbstractSecurityConfig {

	public SecurityConfig(CustomAuthenticationEntryPoint entryPoint,
						  JwtTokenProvider jwtTokenProvider,
						  RedisTokenService redisTokenService) {
		super(entryPoint, jwtTokenProvider, redisTokenService);
	}

	@Override
	protected String[] getPermitAllPaths() {
		return new String[] {
			"/actuator/prometheus"
		};
	}
}