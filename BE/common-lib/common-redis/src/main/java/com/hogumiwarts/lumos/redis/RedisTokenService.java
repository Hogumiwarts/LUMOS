package com.hogumiwarts.lumos.redis;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RedisTokenService {

	private final StringRedisTemplate redisTemplate;

	private static final String REFRESH_TOKEN_PREFIX = "refresh:";
	private static final String ACCESS_TOKEN_BLACKLIST_PREFIX = "blacklist:";

	/**
	 * 리프레시 토큰 저장
	 * Key: refresh:<email>
	 */
	public void saveRefreshToken(String email, String refreshToken, long expiration) {
		String key = REFRESH_TOKEN_PREFIX + email;
		redisTemplate.opsForValue().set(key, refreshToken, expiration, TimeUnit.MILLISECONDS);
	}

	/**
	 * 리프레시 토큰 조회
	 * Key: refresh:<email>
	 */
	public Optional<String> getRefreshToken(String email) {
		String key = REFRESH_TOKEN_PREFIX + email;
		return Optional.ofNullable(redisTemplate.opsForValue().get(key));
	}

	/**
	 * 리프레시 토큰 삭제 (로그아웃)
	 * Key: refresh:<email>
	 */
	public void deleteRefreshToken(String email) {
		String key = REFRESH_TOKEN_PREFIX + email;
		redisTemplate.delete(key);
	}

	/**
	 * AccessToken 블랙리스트 등록
	 * Key: blacklist:<accessToken>
	 */
	public void blacklistAccessToken(String token, long expirationMillis) {
		String key = ACCESS_TOKEN_BLACKLIST_PREFIX + token;
		redisTemplate.opsForValue().set(key, "logout", expirationMillis, TimeUnit.MILLISECONDS);
	}

	/**
	 * AccessToken 블랙리스트 확인
	 * Key: blacklist:<accessToken>
	 */
	public boolean isAccessTokenBlacklisted(String token) {
		String key = ACCESS_TOKEN_BLACKLIST_PREFIX + token;
		return Boolean.TRUE.equals(redisTemplate.hasKey(key));
	}
}

