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
	 * Key: refresh:<memberId>
	 */
	public void saveRefreshToken(Long memberId, String refreshToken, long expiration) {
		String key = REFRESH_TOKEN_PREFIX + memberId;
		redisTemplate.opsForValue().set(key, refreshToken, expiration, TimeUnit.MILLISECONDS);
	}

	/**
	 * 리프레시 토큰 조회
	 * Key: refresh:<memberId>
	 */
	public Optional<String> getRefreshToken(Long memberId) {
		String key = REFRESH_TOKEN_PREFIX + memberId;
		return Optional.ofNullable(redisTemplate.opsForValue().get(key));
	}

	/**
	 * 리프레시 토큰 삭제 (로그아웃)
	 * Key: refresh:<memberId>
	 */
	public void deleteRefreshToken(Long memberId) {
		String key = REFRESH_TOKEN_PREFIX + memberId;
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
