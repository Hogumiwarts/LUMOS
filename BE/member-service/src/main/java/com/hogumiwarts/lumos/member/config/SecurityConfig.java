package com.hogumiwarts.lumos.member.config;

import org.springframework.context.annotation.Configuration;

import com.hogumiwarts.lumos.config.AbstractSecurityConfig;
import com.hogumiwarts.lumos.jwt.CustomAuthenticationEntryPoint;
import com.hogumiwarts.lumos.jwt.JwtTokenProvider;
import com.hogumiwarts.lumos.redis.RedisTokenService;

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
			"/api/email-exists",
			"/api/create",
			"/api/find-by-email",
			"/api/members/{memberId}"
		};
	}
}