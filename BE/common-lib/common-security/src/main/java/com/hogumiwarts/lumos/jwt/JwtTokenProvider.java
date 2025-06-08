package com.hogumiwarts.lumos.jwt;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.hogumiwarts.lumos.exception.CustomException;
import com.hogumiwarts.lumos.exception.ErrorCode;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;

// ... 생략된 import 그대로 유지 ...

@Component
@Getter
public class JwtTokenProvider {

	private final SecretKey secretKey;

	@Value("${jwt.access-token-expiration}")
	private long accessTokenExpiration;

	@Getter
	@Value("${jwt.refresh-token-expiration}")
	private long refreshTokenExpiration;

	public JwtTokenProvider(@Value("${jwt.secret}") String secret) {
		this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
	}

	// JWT 토큰에서 인증 객체 추출
	public Authentication getAuthentication(String token) {
		Claims claims = Jwts.parserBuilder()
			.setSigningKey(secretKey)
			.build()
			.parseClaimsJws(token)
			.getBody();

		String memberId = claims.getSubject();

		UserDetails userDetails = new User(memberId, "", new ArrayList<>());
		return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
	}

	/** 액세스 토큰 생성 (memberId 기반) */
	public String generateAccessToken(Long memberId) {
		return generateToken(memberId, accessTokenExpiration);
	}

	/** 리프레시 토큰 생성 (memberId 기반) */
	public String generateRefreshToken(Long memberId) {
		return generateToken(memberId, refreshTokenExpiration);
	}

	/** 토큰 생성 공통 메서드 (email → memberId) */
	private String generateToken(Long memberId, long expiration) {
		return Jwts.builder()
			.setSubject(String.valueOf(memberId)) // memberId를 문자열로 저장
			.setIssuedAt(new Date())
			.setExpiration(new Date(System.currentTimeMillis() + expiration))
			.signWith(secretKey, SignatureAlgorithm.HS256)
			.compact();
	}

	public boolean validateAccessToken(String token) {
		try {
			Jwts.parserBuilder()
				.setSigningKey(secretKey)
				.build()
				.parseClaimsJws(token);
			return true;
		} catch (ExpiredJwtException e) {
			throw new CustomException(ErrorCode.ACCESS_TOKEN_EXPIRED);
		} catch (MalformedJwtException | UnsupportedJwtException e) {
			throw new CustomException(ErrorCode.INVALID_ACCESS_TOKEN);
		} catch (IllegalArgumentException e) {
			throw new CustomException(ErrorCode.UNAUTHORIZED_USER);
		}
	}

	public void validateRefreshToken(String token) {
		try {
			Jwts.parserBuilder()
				.setSigningKey(secretKey)
				.build()
				.parseClaimsJws(token);
		} catch (ExpiredJwtException e) {
			throw new CustomException(ErrorCode.REFRESH_TOKEN_EXPIRED);
		} catch (SignatureException e) {
			throw new CustomException(ErrorCode.TOKEN_SIGNATURE_INVALID);
		} catch (MalformedJwtException | UnsupportedJwtException e) {
			throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
		} catch (IllegalArgumentException e) {
			throw new CustomException(ErrorCode.UNAUTHORIZED_USER);
		}
	}

	/** 토큰에서 memberId(Long) 추출 */
	public Long getMemberIdFromToken(String token) {
		String subject = Jwts.parserBuilder()
			.setSigningKey(secretKey)
			.build()
			.parseClaimsJws(token)
			.getBody()
			.getSubject();

		return Long.valueOf(subject);
	}

	public String resolveToken(HttpServletRequest request) {
		String bearer = request.getHeader("Authorization");
		return (bearer != null && bearer.startsWith("Bearer ")) ? bearer.substring(7) : null;
	}

	public long getTokenRemainingTime(String token) {
		Date expiration = Jwts.parserBuilder()
			.setSigningKey(secretKey)
			.build()
			.parseClaimsJws(token)
			.getBody()
			.getExpiration();

		return expiration.getTime() - System.currentTimeMillis();
	}
}
