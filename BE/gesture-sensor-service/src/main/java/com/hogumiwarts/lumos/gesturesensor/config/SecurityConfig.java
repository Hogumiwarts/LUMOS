package com.hogumiwarts.lumos.gesturesensor.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import com.hogumiwarts.lumos.config.AbstractSecurityConfig;
import com.hogumiwarts.lumos.jwt.CustomAuthenticationEntryPoint;
import com.hogumiwarts.lumos.jwt.JwtTokenProvider;
import com.hogumiwarts.lumos.redis.RedisTokenService;

@Configuration
public class SecurityConfig extends AbstractSecurityConfig {

	public SecurityConfig(CustomAuthenticationEntryPoint entryPoint, JwtTokenProvider jwtTokenProvider, RedisTokenService redisTokenService) {
		super(entryPoint, jwtTokenProvider, redisTokenService);
	}

	@Value("${spring.profiles.active:default}")
	private String activeProfile;

	@Override
	protected String[] getPermitAllPaths() {
		return activeProfile.equals("dev") ?
			new String[] {"/**"}
			: new String[] {};
	}
}